import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public boolean cadastrar(Produto produto) {
        if (produto == null
                || produto.getNome() == null
                || produto.getNome().isBlank()
                || produto.getCategoria() == null
                || produto.getCategoria().isBlank()
                || produto.getPreco() < 0
                || produto.getQuantidadeEstoque() < 0) {
            return false;
        }

        String sql = "INSERT INTO produtos (nome, descricao, categoria, preco, quantidade_estoque, ativo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoMySQL.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, produto.getNome());
            ps.setString(2, produto.getDescricao());
            ps.setString(3, produto.getCategoria());
            ps.setDouble(4, produto.getPreco());
            ps.setInt(5, produto.getQuantidadeEstoque());
            ps.setBoolean(6, produto.isAtivo());

            int linhasAfetadas = ps.executeUpdate();
            if (linhasAfetadas == 0) {
                return false;
            }

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    produto.setId(chaves.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Produto> listar() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, categoria, preco, quantidade_estoque, ativo "
                + "FROM produtos ORDER BY nome";

        try (Connection conexao = ConexaoMySQL.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setCategoria(rs.getString("categoria"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                produto.setAtivo(rs.getBoolean("ativo"));
                produtos.add(produto);
            }
        } catch (SQLException e) {
            return new ArrayList<>();
        }

        return produtos;
    }

    public boolean atualizarEstoque(int idProduto, int novaQuantidade) {
        if (idProduto <= 0 || novaQuantidade < 0) {
            return false;
        }
        String sql = "UPDATE produtos SET quantidade_estoque = ? WHERE id = ?";

        try (Connection conexao = ConexaoMySQL.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, novaQuantidade);
            ps.setInt(2, idProduto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
