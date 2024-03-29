# Node-colors-autocomplete
Description:
This language server provides code completion features for NodeC documents, specifically focusing on assisting with completion of "nodes" property based on "nodecolors" property.

example 1:
nodecolors:red
nodes:[]
Now The at nodes:[] auto completes to the strings given at nodecolors to be as the following:
nodecolors:red
nodes:[red]

example 2:
nodecolors:red, green
nodes: [red]
Now The at nodes:[red] auto completes to the strings given at nodecolors to be as the following:
nodecolors: red, green
nodes: [red, green]

Completion Logic:
Analyzes document content to extract "nodecolors" and "nodes" values.
Suggests available node colors for completion based on context.
Considers colors already used in "nodes" to avoid duplicates.
