| Key          | Value                                                                           |
|--------------|---------------------------------------------------------------------------------|
| Date         | 27.11.2024                                                                      |
| Time         | 13:45                                                                           |
| Location     | DW PC1 Cubicle 6                                                                |
| Chair        | Malmare Radu                                                                    |
| Minute Taker | Untu Serban                                                                     |
| Attendees    | Michał Barciński <br/> Lukasz Kasprzak <br/> Vidhi Khakhar <br/> Yoanna Dyakova |


# Agenda Items

## Opening by chair, check-in (40s)

## Any announcements by the team (1min 30s)

- We talked about what everyone worked on generally

## Approval of the agenda - Anything to add? (25s)

- Nothing to add

## Announcements by the TA (45s)

- Make sure to upload the CoC
- Make sure to meet the LOC quota

## Presentation of the current app to the TA (6min 30s)

- Radu: rendered Markdown (commonmark).
- Michal: button that creates a file.
- Lukasz: Working 
- We only need the Note entity for the basic requirements.
- Yoanna: Started work on the request handler.
- Vidhi: UI but it needs to be ported to the project.
- There should be no user authentication.

## Talking points

### Final remarks on the Code of Conduct (1min 30s)

- We could add what is mentioned in Serban's comment during the lab.
- Vidhi also had a suggestion.

### What could be a short summary of any problem during the Markdown rendering (2 min)

- If you want to include a normal asterisk (*) you can just escape it using escape sequences.

### How to create and delete a file, as well as how to change its title (2 min)

- Michal worked on note creation. So far it stores new notes as local files.
- Vidhi could first recreate the client in SceneBuilder and then upload it to the project.

### How the client should look like (3 min)

- Highlighting words that you search for.
- Tags next to note names.
- Magnifying glass next to search bar.
- User icons over text on buttons.

### How will the file be saved in the database (4min 30s)

- We store the text of a note as a plain string in the database.
- User to collection and collection to note is many to many.

## Summarize action points: Who, what, when? (3min 15s)

Here is what everyone will work on until Friday:
- Michal: Help with the interface.
- Yoanna: Help Lukasz with testing the database.
- Serban & Lukasz: Backend and endpoints.
- Vidhi: Redo UI in SceneBuilder (Michal can help).
- Radu: Work on short summary about Markdown problems.

## Question round, anything to add before closing? (2 min)

- Knockout criteria:
    - FXML does not count for the line count. Only Java counts.
    - Vidhi could write controllers to meet the quota.
    - Testing is good for reaching the LOC quota.

## Feedback round (1min 30s)

- Fewer dependencies between tasks.
- We should work together on critical tasks.
- We need to present a shippable product to not fail that rubric.

## Closure (30s)

- End of meeting.