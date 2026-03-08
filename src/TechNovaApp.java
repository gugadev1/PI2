import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TechNovaApp {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Mantem o look and feel padrao caso o sistema nao permita alteracao.
        }

        SwingUtilities.invokeLater(() -> {
            TechNovaDesktopUI janela = new TechNovaDesktopUI();
            janela.setVisible(true);
        });
    }
}
