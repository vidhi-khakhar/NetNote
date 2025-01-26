# CSEP Template Project

This repository contains the template for the CSE project. Please extend this README.md with sufficient instructions that will illustrate for your TA and the course staff how they can run your project.

To run the template project from the command line, you either need to have [Maven](https://maven.apache.org/install.html) installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute

	mvn clean install

to package and install the artifacts for the three subprojects. Afterwards, you can run ...

	cd server
	mvn spring-boot:run

to start the server or ...

	cd client
	mvn javafx:run

to run the client. Please note that the server needs to be running, before you can start the client.

Once this is working, you can try importing the project into your favorite IDE.

# Instructions

This part of the `README.md` has been added by the students.
It contains useful information about the application, for the examiners.

*Note: The project should be run using Java 23 or above*.

## Accessibility

- Tooltips will appear when hovering over buttons with no text. This helps keep the interface more compact.
- The self-destructing notifications use a separate stage so they might affect the appearance of other opened windows.

### Undo / redo

Both *undo* and *redo* have been implemented for the following actions:
- Creating a note
- Deleting a note
- Adding a file to a note
- Moving a note to a different collection

To undo an action, press `Ctrl`+`Z`.

To redo an action, press `Ctrl`+`Y`.

*Note: Undo / redo also work for the note editor. When a text input is selected, undo / redo is for text only and 
will not take into account the previously mentioned actions.*

### Shortcuts

*Note: The list of shortcuts can be accessed from the application by pressing the "?" button in the main view.*

*Note: To use the shortcut for cycling through languages, the dropdown menus must not be selected*.

Here is a list of all shortcuts supported by the application:
- Search in notes: `Esc` / `Ctrl`+`F`
- Undo action: `Ctrl`+`Z`
- Redo action: `Ctrl`+`Y`
- Add a note: `Ctrl`+`N` (in the main view)
- Delete a note: `Ctrl`+`D` (in the main view)
- Move a note to a different collection: `Ctrl`+`M`
- Refresh the local data: `Ctrl`+`R`
- Toggle bold text: `Ctrl`+`B`
- Toggle italic text: `Ctrl`+`I`
- Toggle underlined text: `Ctrl`+`U`
- Switch to next language in list: `Ctrl`+`L`
- Switch to previous language in list: `Ctrl`+`Shift`+`L`
- Open the collections menu: `Ctrl`+`Shift`+`C`
- Toggle Markdown problems: `Ctrl`+`Alt`+`M`
- Focus on the list of notes: `Alt`+`N`
- Focus on the note editor: `Alt`+`E`
- Focus on the list of files: `Alt`+`F`
- Focus on the collection dropdown: `Alt`+`C`
---
- Add a collection: `Ctrl`+`N` (in the collection menu)
- Remove a collection: `Ctrl`+`D` (in the collection menu)
- Save current action: `Ctrl`+`S`
- Focus on the list of collections (in the collection menu): `Alt`+`L`

*Note: Markdown problems are issues related to the rendering of Markdown.*
*For example not leaving a space between the "#" symbol and the heading name is considered a problem*.

## Meaningful extensions

The following sections detail what we implemented as part of the meaningful additions for the 3 extensions we worked on.

### Multi-collection

- Collections can be stored and accessed from different servers, not just the local server.
- To share collections between multiple servers, the devices must be connected to the same network.
- The network menu allows you to copy the local address so you can share it with the people that you want to share the collection with.

*Note: Removing a collection from the collections menu does not delete it on the server.*

*Note: When a note is created, it will be added both to the current collection and the default collection.*

*Note: The refresh button can be used to fetch new data from the server.*

#### Network menu

- Additionally, the *Network* menu can be used to see all the notes / collections / files that are stored on a given server.
- The refresh button should be used to update the list of available servers, after collections have been added or removed from the collection menu.
- We opted for this approach instead of providing an "All" option in the collection dropdown, which is how it was shown in the provided design.

### Embedded files

- It is possible to see the file type and file size for all files added to a note.

### Live language switch

- There are 4 available languages: English, Dutch, Romanian and Polish.
- There is a shortcut for quickly cycling through languages: `Ctrl`+`L` (and `Ctrl`+`Shift`+`L` for the opposite direction)
- A notification is shown when switching between languages.

## Stylesheets

It is possible to style the rendered Markdown when following these rules:
- The added `.css` file must be added in the project root (in the same directory as this file).
- If multiple `.css` files are present, only the first one will be used.
- A notification will be shown when the app is started, informing about which stylesheets are applied.

## Troubleshooting

Try these if the application stops working as intended / crashes:
- Delete the `config.json` file in the project root.
- Delete the H2 database files (`.db`).

*Note: It is safer to run the tests while the application server is turned off.*
