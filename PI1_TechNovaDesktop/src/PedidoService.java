import java.util.List;

public class PedidoService {

    public Pedido criarPedido(Cliente cliente, List<ItemPedido> itens) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente obrigatorio para criar pedido.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Pedido deve conter ao menos um item.");
        }

        validarEstoque(itens);
        baixarEstoque(itens);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        for (ItemPedido item : itens) {
            pedido.adicionarItem(item);
        }
        return pedido;
    }

    public void atualizarStatusPedido(Pedido pedido, StatusPedido novoStatus) {
        if (pedido == null || novoStatus == null) {
            throw new IllegalArgumentException("Pedido e status sao obrigatorios.");
        }
        pedido.setStatus(novoStatus);
    }

    private void validarEstoque(List<ItemPedido> itens) {
        for (ItemPedido item : itens) {
            Produto produto = item.getProduto();
            if (produto == null) {
                throw new IllegalArgumentException("Item com produto invalido.");
            }
            if (item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
            }
            if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new IllegalStateException("Estoque insuficiente para o produto: " + produto.getNome());
            }
        }
    }

    private void baixarEstoque(List<ItemPedido> itens) {
        for (ItemPedido item : itens) {
            Produto produto = item.getProduto();
            int novoEstoque = produto.getQuantidadeEstoque() - item.getQuantidade();
            produto.setQuantidadeEstoque(novoEstoque);
        }
    }
}
