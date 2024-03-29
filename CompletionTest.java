import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CompletionTest {

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

    public static void main(String[] args) {
        String content = "";
        try {
            File myObj = new File("test.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                content += myReader.nextLine() + "\n";
                System.out.println(content);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
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

                // Check if the nodes value is empty
                if (nodes.length <= 1 && nodes[0].isEmpty()) {
                    // Auto-complete the nodes value with the node colors
                    for (String nodeColor : nodeColors) {
                        System.out.println(" nodeColor= " + nodeColor);
                    }
                } else {
                    // add the colors that are not in the nodes list
                    if (!nodes[nodes.length - 1].equals(nodeColors[nodeColors.length - 1])) {
                        // Auto-complete the last node value with the remaining node colors
                        List<String> remainingNodeColors = new ArrayList<>(Arrays.asList(nodeColors));
                        remainingNodeColors.removeAll(Arrays.asList(nodes));

                        for (String remainingNodeColor : remainingNodeColors) {
                            System.out.println("remainingNodeColor= "
                                    + remainingNodeColor);
                        }
                    }
                }
            }

        }
    }

}