package org.nodeC.ls.langserver;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.Arrays;
import java.util.HashMap;

public class NodeCTextDocumentService implements TextDocumentService, LanguageClientAware {
    private LanguageClient languageClient;
    private Map<String, TextDocumentItem> documentRegistry = new HashMap<>();

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {
        List<CompletionItem> suggestions = new ArrayList<>();
        CompletableFuture<Either<List<CompletionItem>, CompletionList>> future = new CompletableFuture<>();

        // Get the text document content
        String content = getTextDocumentContent(position);
        // Implement logic to provide completion suggestions based on the content
        suggestions = completionLogic(content);
        // Create the completion list
        CompletionList completionList = new CompletionList(false, suggestions);
        future.complete(Either.forRight(completionList));

        return future;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        TextDocumentItem document = params.getTextDocument();
        String uri = document.getUri();
        documentRegistry.put(uri, document);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        VersionedTextDocumentIdentifier documentIdentifier = params.getTextDocument();
        String uri = documentIdentifier.getUri();
        TextDocumentItem document = documentRegistry.get(uri);

        if (document != null) {
            document.setVersion(documentIdentifier.getVersion());
            document.setText(params.getContentChanges().get(0).getText());
        }
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        documentRegistry.remove(uri);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        // TODO: Implement logic for when the document is saved
    }

    @Override
    public void connect(LanguageClient languageClient) {
        this.languageClient = languageClient;
    }

    // Helper method to extract the Node values property from the content
    public static String extractNodesValues(String content, String property) {
        int propertyIndex = content.indexOf(property);
        if (propertyIndex != -1) {
            int LbracketIndex = content.indexOf("[", propertyIndex);
            int RbracketIndex = content.indexOf("]", LbracketIndex);
            if (LbracketIndex != -1 && LbracketIndex != -1) {
                return content.substring(LbracketIndex + 1, RbracketIndex).trim();
            }
        }
        return null;
    }

    // Helper method to extract the Node Colors Values property from the content
    public static String extractNodeColorsValues(String content, String property) {
        int propertyIndex = content.indexOf(property);
        if (propertyIndex != -1) {
            int LIndex = content.indexOf(":", propertyIndex);
            int RbracketIndex = content.indexOf("\n", LIndex);
            if (LIndex != -1 && RbracketIndex != -1) {
                return content.substring(LIndex + 1, RbracketIndex).trim();
            }
        }
        return null;
    }

    public String getTextDocumentContent(CompletionParams position) {
        TextDocumentIdentifier textDocumentIdentifier = position.getTextDocument();
        String uri = textDocumentIdentifier.getUri();
        TextDocumentItem document = documentRegistry.get(uri);
        String content = document.getText();
        return content;
    }

    private List<CompletionItem> completionLogic(String content) {
        List<CompletionItem> suggestions = new ArrayList<>();

        // Check if the content contains "nodecolors" and "nodes"
        if (content.contains("nodecolors") && content.contains("nodes")) {
            // Extract the values from "nodecolors" and "nodes"
            String nodeColorsValue = extractNodeColorsValues(content, "nodecolors");
            String nodesValue = extractNodesValues(content, "nodes");

            if (nodeColorsValue != null && nodesValue != null) {
                // Split the node colors and nodes values into separate strings
                String[] nodeColors = nodeColorsValue.split(",");
                String[] nodes = nodesValue.split(",");

                // Trim the whitespaces from the node colors and nodes
                for (int i = 0; i < nodeColors.length; i++) {
                    nodeColors[i] = nodeColors[i].trim();
                }

                for (int i = 0; i < nodes.length; i++) {
                    nodes[i] = nodes[i].trim();
                }
                // Get the index of "nodes:[]"
                int nodesIndex = content.indexOf("nodes:[]");
                // Check if the nodes value is empty
                if (nodes.length == 1 && nodes[0].isEmpty()) {
                    // Auto-complete the nodes value with the node colors

                    for (String nodeColor : nodeColors) {
                        CompletionItem item = new CompletionItem();
                        item.setLabel(nodeColor);
                        item.setInsertText(nodeColor);
                        // wrapping the textEdit in an Either object
                        TextEdit te = new TextEdit(new Range(new Position(nodesIndex, 6), new Position(nodesIndex, 6)),
                                nodeColor + ",");
                        Either<TextEdit, InsertReplaceEdit> textEdit = Either.forLeft(te);
                        item.setTextEdit(textEdit);
                        suggestions.add(item);
                    }
                } else {
                    // add the colors that are not in the nodes list
                    if (!nodes[nodes.length - 1].equals(nodeColors[nodeColors.length - 1])) {
                        // Auto-complete the last node value with the remaining node colors
                        List<String> remainingNodeColors = new ArrayList<>(Arrays.asList(nodeColors));
                        remainingNodeColors.removeAll(Arrays.asList(nodes));

                        for (String remainingNodeColor : remainingNodeColors) {
                            CompletionItem item = new CompletionItem();
                            item.setLabel(remainingNodeColor);
                            item.setInsertText(remainingNodeColor);
                            // wrapping the textEdit in an Either object
                            TextEdit te = new TextEdit(
                                    new Range(new Position(nodesIndex, 6), new Position(nodesIndex, 6)),
                                    remainingNodeColor + ",");
                            Either<TextEdit, InsertReplaceEdit> textEdit = Either.forLeft(te);
                            item.setTextEdit(textEdit);
                            suggestions.add(item);
                        }
                    }
                }
            }
        }
        return suggestions;
    }

}