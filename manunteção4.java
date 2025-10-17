import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class ControleTarefasGUI extends JFrame {

    private static final String NOME_ARQUIVO = "tarefas.txt";
    private static final Color COR_FUNDO = new Color(245, 245, 245); 
    private static final Color COR_PAINEL_SUPERIOR = new Color(70, 130, 180);
    private static final Color COR_BOTAO = new Color(95, 158, 160); 
    private static final Color COR_TEXTO_BOTAO = Color.WHITE;
    private static final Font FONTE_PADRAO = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONTE_LISTA = new Font("Segoe UI", Font.PLAIN, 16);

    private DefaultListModel<String> listModel;
    private JList<String> listaTarefas;
    private JTextField campoNovaTarefa;
    private JButton botaoAdicionar;
    private JButton botaoEditar;
    private JButton botaoExcluir;

    public ControleTarefasGUI() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.out.println("Nimbus Look and Feel não encontrado. Usando o padrão.");
        }

        setTitle("Sistema de Controle de Tarefas");
        setSize(550, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().setBackground(COR_FUNDO);


        listModel = new DefaultListModel<>();

        listaTarefas = new JList<>(listModel);
        listaTarefas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaTarefas.setFont(FONTE_LISTA);
        listaTarefas.setCellRenderer(new TarefaListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(listaTarefas);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); 

        campoNovaTarefa = new JTextField();
        campoNovaTarefa.setFont(FONTE_PADRAO);
        campoNovaTarefa.setBorder(BorderFactory.createCompoundBorder(
            campoNovaTarefa.getBorder(), 
            new EmptyBorder(5, 5, 5, 5))
        );

        botaoAdicionar = new JButton("Adicionar");
        botaoEditar = new JButton("Editar Tarefa Selecionada");
        botaoExcluir = new JButton("Excluir Tarefa Selecionada");

        stylizeButton(botaoAdicionar);
        stylizeButton(botaoEditar);
        stylizeButton(botaoExcluir);


        JPanel painelSuperior = new JPanel(new BorderLayout(10, 0));
        painelSuperior.setBackground(COR_PAINEL_SUPERIOR);
        painelSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));
        painelSuperior.add(new JLabel("Nova Tarefa:"), BorderLayout.WEST); 
        painelSuperior.add(campoNovaTarefa, BorderLayout.CENTER);
        painelSuperior.add(botaoAdicionar, BorderLayout.EAST);

        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelInferior.setBackground(COR_FUNDO);
        painelInferior.add(botaoEditar);
        painelInferior.add(botaoExcluir);

        add(painelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(painelInferior, BorderLayout.SOUTH);

        setupActionListeners();

        carregarTarefas();
    }

    private void stylizeButton(JButton button) {
        button.setFont(FONTE_PADRAO);
        button.setBackground(COR_BOTAO);
        button.setForeground(COR_TEXTO_BOTAO);
        button.setFocusPainted(false); 
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        button.setBorder(new EmptyBorder(5, 15, 5, 15));
    }
    
    private void setupActionListeners() {
        botaoAdicionar.addActionListener(e -> adicionarTarefa());
        campoNovaTarefa.addActionListener(e -> adicionarTarefa());
        botaoEditar.addActionListener(e -> editarTarefa());
        botaoExcluir.addActionListener(e -> excluirTarefa());
    }

    private void adicionarTarefa() {
        String novaTarefa = campoNovaTarefa.getText().trim();
        if (!novaTarefa.isEmpty()) {
            listModel.addElement(novaTarefa);
            campoNovaTarefa.setText("");
            salvarTarefas();
        } else {
            JOptionPane.showMessageDialog(this, "A descrição da tarefa não pode estar vazia.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarTarefa() {
        int selectedIndex = listaTarefas.getSelectedIndex();
        if (selectedIndex != -1) {
            String tarefaAtual = listModel.getElementAt(selectedIndex);
            String novaDescricao = JOptionPane.showInputDialog(this, "Edite a sua tarefa:", tarefaAtual);
            
            if (novaDescricao != null && !novaDescricao.trim().isEmpty()) {
                listModel.set(selectedIndex, novaDescricao.trim());
                salvarTarefas();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma tarefa para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirTarefa() {
        int selectedIndex = listaTarefas.getSelectedIndex();
        if (selectedIndex != -1) {
            int confirmacao = JOptionPane.showConfirmDialog(
                this, 
                "Tem certeza que deseja excluir esta tarefa?", 
                "Confirmar Exclusão", 
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirmacao == JOptionPane.YES_OPTION) {
                listModel.remove(selectedIndex);
                salvarTarefas();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma tarefa para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void salvarTarefas() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(NOME_ARQUIVO))) {
            for (int i = 0; i < listModel.getSize(); i++) {
                writer.println(listModel.getElementAt(i));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar tarefas: " + e.getMessage(), "Erro de Arquivo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTarefas() {
        File arquivo = new File(NOME_ARQUIVO);
        if (arquivo.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    listModel.addElement(linha);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar tarefas: " + e.getMessage(), "Erro de Arquivo", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ControleTarefasGUI().setVisible(true));
    }

    // --- Classe Interna para Renderizar a Lista de forma customizada ---
    private static class TarefaListCellRenderer extends DefaultListCellRenderer {
        private static final Color COR_SELECAO = new Color(173, 216, 230); // LightBlue
        private static final Color COR_ITEM_PAR = new Color(255, 255, 255); // Branco
        private static final Color COR_ITEM_IMPAR = new Color(240, 240, 240); // Cinza bem claro

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            // Chama o método da superclasse para obter o componente padrão
            Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            // Define o texto
            setText(value.toString());

            // Adiciona padding interno à célula
            setBorder(new EmptyBorder(5, 10, 5, 10));

            // Lógica de cores
            if (isSelected) {
                setBackground(COR_SELECAO);
                setForeground(Color.BLACK);
            } else {
                // Alterna as cores de fundo para cada linha (efeito "zebra")
                setBackground(index % 2 == 0 ? COR_ITEM_PAR : COR_ITEM_IMPAR);
                setForeground(Color.DARK_GRAY);
            }
            
            return renderer;
        }
    }
}
