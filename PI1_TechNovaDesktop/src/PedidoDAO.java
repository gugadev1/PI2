import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PedidoDAO {

    public boolean cadastrar(Pedido pedido) {
        if (pedido == null || pedido.getCliente() == null || pedido.getItens().isEmpty()) {
            return false;
        }

        for (ItemPedido item : pedido.getItens()) {
            if (item == null
                    || item.getProduto() == null
                    || item.getProduto().getId() <= 0
                    || item.getQuantidade() <= 0
                    || item.getPrecoUnitario() < 0) {
                return false;
            }
        }

        pedido.recalcularTotal();

        String sqlPedido = "INSERT INTO pedidos (cliente_id, usuario_responsavel_id, status, total) VALUES (?, ?, ?, ?)";
        String sqlItem = "INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario, subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlBaixaEstoque = "UPDATE produtos SET quantidade_estoque = quantidade_estoque - ? "
                + "WHERE id = ? AND quantidade_estoque >= ?";

        try (Connection conexao = ConexaoMySQL.obterConexao()) {
            conexao.setAutoCommit(false);

            try {
                int idPedido;
                try (PreparedStatement psPedido = conexao.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                    psPedido.setInt(1, pedido.getCliente().getId());
                    psPedido.setNull(2, java.sql.Types.INTEGER);
                    psPedido.setString(3, pedido.getStatus().name());
                    psPedido.setDouble(4, pedido.getTotal());

                    if (psPedido.executeUpdate() == 0) {
                        conexao.rollback();
                        return false;
                    }

                    try (ResultSet chaves = psPedido.getGeneratedKeys()) {
                        if (!chaves.next()) {
                            conexao.rollback();
                            return false;
                        }
                        idPedido = chaves.getInt(1);
                        pedido.setId(idPedido);
                    }
                }

                try (PreparedStatement psItem = conexao.prepareStatement(sqlItem);
                     PreparedStatement psBaixaEstoque = conexao.prepareStatement(sqlBaixaEstoque)) {

                    for (ItemPedido item : pedido.getItens()) {
                        psItem.setInt(1, idPedido);
                        psItem.setInt(2, item.getProduto().getId());
                        psItem.setInt(3, item.getQuantidade());
                        psItem.setDouble(4, item.getPrecoUnitario());
                        psItem.setDouble(5, item.getSubtotal());
                        psItem.addBatch();

                        psBaixaEstoque.setInt(1, item.getQuantidade());
                        psBaixaEstoque.setInt(2, item.getProduto().getId());
                        psBaixaEstoque.setInt(3, item.getQuantidade());
                        psBaixaEstoque.addBatch();
                    }

                    psItem.executeBatch();
                    int[] resultadosBaixa = psBaixaEstoque.executeBatch();
                    for (int resultado : resultadosBaixa) {
                        if (resultado == 0 || resultado == Statement.EXECUTE_FAILED) {
                            throw new SQLException("Falha ao baixar estoque de um ou mais produtos.");
                        }
                    }
                }

                conexao.commit();
                return true;
            } catch (SQLException e) {
                conexao.rollback();
                return false;
            } finally {
                conexao.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Pedido> listarPorCliente(int idCliente) {
        if (idCliente <= 0) {
            return new ArrayList<>();
        }

        String sql = "SELECT p.id AS pedido_id, p.data_criacao, p.status, p.total, "
                + "i.produto_id, i.quantidade, i.preco_unitario, "
                + "pr.nome AS produto_nome, pr.descricao, pr.categoria, pr.ativo "
                + "FROM pedidos p "
                + "LEFT JOIN itens_pedido i ON i.pedido_id = p.id "
                + "LEFT JOIN produtos pr ON pr.id = i.produto_id "
                + "WHERE p.cliente_id = ? "
                + "ORDER BY p.data_criacao DESC, p.id DESC";

        Map<Integer, Pedido> mapaPedidos = new LinkedHashMap<>();

        try (Connection conexao = ConexaoMySQL.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idPedido = rs.getInt("pedido_id");
                    Pedido pedido = mapaPedidos.get(idPedido);

                    if (pedido == null) {
                        pedido = new Pedido();
                        pedido.setId(idPedido);

                        Cliente cliente = new Cliente();
                        cliente.setId(idCliente);
                        pedido.setCliente(cliente);

                        Timestamp timestamp = rs.getTimestamp("data_criacao");
                        if (timestamp != null) {
                            pedido.setDataCriacao(timestamp.toLocalDateTime());
                        }

                        String statusBanco = rs.getString("status");
                        try {
                            pedido.setStatus(StatusPedido.valueOf(statusBanco));
                        } catch (IllegalArgumentException e) {
                            pedido.setStatus(StatusPedido.EM_PROCESSAMENTO);
                        }

                        pedido.setTotal(rs.getDouble("total"));
                        mapaPedidos.put(idPedido, pedido);
                    }

                    int idProduto = rs.getInt("produto_id");
                    if (rs.wasNull()) {
                        continue;
                    }

                    Produto produto = new Produto();
                    produto.setId(idProduto);
                    produto.setNome(rs.getString("produto_nome"));
                    produto.setDescricao(rs.getString("descricao"));
                    produto.setCategoria(rs.getString("categoria"));
                    produto.setAtivo(rs.getBoolean("ativo"));

                    ItemPedido item = new ItemPedido(
                            produto,
                            rs.getInt("quantidade"),
                            rs.getDouble("preco_unitario")
                    );
                    pedido.getItens().add(item);
                }
            }
        } catch (SQLException e) {
            return new ArrayList<>();
        }

        return new ArrayList<>(mapaPedidos.values());
    }

    public boolean atualizarStatus(int idPedido, StatusPedido novoStatus) {
        if (idPedido <= 0 || novoStatus == null) {
            return false;
        }
        String sql = "UPDATE pedidos SET status = ? WHERE id = ?";

        try (Connection conexao = ConexaoMySQL.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, novoStatus.name());
            ps.setInt(2, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
