//package com.jmathanim.editor;
//
//import com.jmathanim.mathobjects.Shape;
//import org.fife.ui.autocomplete.*;
//import org.fife.ui.rsyntaxtextarea.*;
//import org.fife.ui.rtextarea.*;
//
//import javax.swing.*;
//import javax.swing.text.BadLocationException;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.lang.reflect.Method;
//
//public class JavaCodeEditorWithAutocomplete {
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(JavaCodeEditorWithAutocomplete::createAndShowGUI);
//
//    }
//
//    private static void createAndShowGUI() {
//        JFrame frame = new JFrame("Editor Java con Autocompletado");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(800, 600);
//
//        RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
//        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
//        textArea.setCodeFoldingEnabled(true);
//        textArea.setAntiAliasingEnabled(true);
//
//        RTextScrollPane scrollPane = new RTextScrollPane(textArea);
//        frame.add(scrollPane);
//
//        // Añadir autocompletado
//        CompletionProvider provider = createCompletionProvider();
//        AutoCompletion ac = new AutoCompletion(provider);
//        ac.setShowDescWindow(true); // Opcional, muestra tooltip de Javadoc
//        ac.setAutoActivationEnabled(false); // Para no hacerlo automático con letras
//        ac.install(textArea);
//
//        configureTabAutocomplete(textArea, ac);
//
//        // Mostrar
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//
//        textArea.setText(""+
//            "public class Main {"+
//                "public static void main(String[] args) {"+
//                    "// Prueba autocompletado aquí"+
//                "}"+
//            "}"+
//        ");");
//    }
//
//
//    private static void configureTabAutocomplete(RSyntaxTextArea textArea, AutoCompletion ac) {
//        InputMap im = textArea.getInputMap();
//        ActionMap am = textArea.getActionMap();
//
//        im.put(KeyStroke.getKeyStroke("TAB"), "custom-tab-autocomplete");
//
//        am.put("custom-tab-autocomplete", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    int caretPos = textArea.getCaretPosition();
//                    String text = textArea.getText(0, caretPos);
//
//                    // Extraer palabra justo antes del cursor
//                    int start = caretPos - 1;
//                    while (start >= 0 && Character.isJavaIdentifierPart(text.charAt(start))) {
//                        start--;
//                    }
//                    start++;
//                    String prefix = caretPos > start ? text.substring(start, caretPos) : "";
//
//                    if (!prefix.isEmpty()) {
//                        ac.doCompletion(); // Mostrar menú de autocompletado
//                    } else {
//                        textArea.replaceSelection("    "); // Insertar 4 espacios (tab simulado)
//                    }
//                } catch (BadLocationException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });
//    }
//
//    private static CompletionProvider createCompletionProvider() {
//        DefaultCompletionProvider provider = new DefaultCompletionProvider();
//
//        // Palabras clave básicas
//        String[] keywords = {"public", "private", "class", "static", "void", "int", "new", "return"};
//        for (String kw : keywords) {
//            provider.addCompletion(new BasicCompletion(provider, kw));
//        }
//
//        // Añadir clases de Java conocidas
////        addClassCompletions(provider, java.lang.Math.class);
////        addClassCompletions(provider, java.lang.String.class);
////        addClassCompletions(provider, java.lang.System.class);
////        addClassCompletions(provider, MathObject.class);
//        addClassCompletions(provider, Shape.class);
////        addClassCompletions(provider, Animation.class);
//
//        return provider;
//    }
//    private static void addClassCompletions(DefaultCompletionProvider provider, Class<?> cls) {
//        for (var method : cls.getMethods()) {
//            String signature = method.getName() + "(";
//            var params = method.getParameters();
//            for (int i = 0; i < params.length; i++) {
//                signature += params[i].getType().getSimpleName();
//                if (i < params.length - 1) signature += ", ";
//            }
//            signature += ")";
//            System.out.println("adding "+method.getName()+", "+method.getReturnType().getSimpleName());
//            provider.addCompletion(new FunctionCompletion(provider, method.getName(), method.getReturnType().getSimpleName()));
//        }
//
//        for (var field : cls.getFields()) {
//            provider.addCompletion(new BasicCompletion(provider, field.getName()));
//        }
//    }
//
//    private static String generateDocForMethod(Method method) {
//        StringBuilder doc = new StringBuilder("<html>");
//        doc.append("<b>").append(method.getName()).append("</b><br>");
//        doc.append("Retorna: ").append(method.getReturnType().getSimpleName()).append("<br>");
//        doc.append("Parámetros: ");
//        if (method.getParameterCount() == 0) {
//            doc.append("ninguno");
//        } else {
//            doc.append("<ul>");
//            for (var param : method.getParameters()) {
//                doc.append("<li>").append(param.getType().getSimpleName()).append(" ").append(param.getName()).append("</li>");
//            }
//            doc.append("</ul>");
//        }
//        doc.append("</html>");
//        return doc.toString();
//    }
//
//}
