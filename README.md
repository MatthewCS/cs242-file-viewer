# cs242-file-viewer
Simple JavaFX file viewer for my CS242 class
### Authors: Matthew Sprague, Aaron Jones  

This is the repo for a GUI File Viewer developed using JavaFX for our CS242 class.

The FileViewer has a complete, working image viewer (supporting .PNGs, .JPGs, .GIFs, .BMPs), CSV file viewer, and JSON file viewer (complete with syntax highlighting!)

The image viewer and CSV file viewer are done using fairly simple, standard implentations. The JSON file viewer is done by embedding a Swing node into the JavaFX application, as JavaFX has no native support for syntax highlighting.

Syntax highlighting is done using a purpose-built JSON tokenizer.

![Image viewer](/image-viewer.png)

![CSV viewer](/csv-viewer.png)

![JSON viewer](/json-viewer.png)
