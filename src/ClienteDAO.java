import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public boolean cadastrar(Cliente cliente) {
        if (cliente == null
                || cliente.getNome() == null
                || cliente.getNome().isBlank()
                || cliente.getEmail() == null
                || cliente.getEmail().isBlank()
                || cliente.getTelefone() == null
                || cliente.getTelefone().isBlank()) {
            return false;
        }

        String sql = "INSERT INTO clientes (nome, email, senha_hash, telefone, ativo) VALUES (?, ?, ?, ?, 1)";

        try (Connection conexao = ConexaoMySQL.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getEmail());
            ps.setString(3, cliente.getSenhaHash());
            ps.setString(4, cliente.getTelefone());

            int linhasAfetadas = ps.executeUpdate();
            if (linhasAfetadas == 0) {
                return false;
            }

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    cliente.setId(chaves.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Cliente> listar() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nome, email, senha_hash, telefone FROM clientes WHERE ativo = 1 ORDER BY nome";

        try (Connection conexao = ConexaoMySQL.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setEmail(rs.getString("email"));
                cliente.setSenhaHash(rs.getString("senha_hash"));
                cliente.setTelefone(rs.getString("telefone"));
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            return new ArrayList<>();
        }

        return clientes;
    }

    public Cliente buscarPorId(int id) {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT id, nome, email, senha_hash, telefone FROM clientes WHERE id = ? AND ativo = 1";

        try (Connection conexao = ConexaoMySQL.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setEmail(rs.getString("email"));
                cliente.setSenhaHash(rs.getString("senha_hash"));
                cliente.setTelefone(rs.getString("telefone"));
                return cliente;
            }
        } catch (SQLException e) {
            return null;
        }
    }
}
