| Key          | Value                                                                       |
|--------------|-----------------------------------------------------------------------------|
| Date         | 04.12.2024                                                                  |
| Time         | 13:45                                                                       |
| Location     | DW PC1 Cubicle 6                                                            |
| Chair        | Vidhi Khakhar                                                               |
| Minute Taker | Yoanna Dyakova                                                              |
| Attendees    | Michał Barciński <br/> Lukasz Kasprzak <br/> Serban Untu <br/> Radu Malmare |

# Estimated Time: 50 minutes

# Agenda Items

### - Opening by chair (1 min)

### - Check-in : How is everyone doing ? (1 min)

### - Any announcements by the team (3 min) --> tell the TA we uploaded COC.

- Serban: reorganized the main controller and implemented undo deleted note
- Michal: fixing bug, waiting for the searching algorithm
- Yoanna: did API requests from client side 
- Lukas: will do the search, will review Yoanna's merge request
- Radu: working on markdown problems 
- Vidhi: working on usability criteria - dark theme, keyboard shortcuts (without using mouse)

### - Approval of the agenda - Anything to add? (2 min)

- Radu: suggestion for automatic saving and removing the button for that

### - Announcements by the TA (4 min)

- update teammates about plans for meeting in week 6
- to be discussed in Mattermost: week 7 lab conflicts with resit for IP
- knockout criteria - you receive Outlook email for that
- Buddy Check due December 6 -  AID model - strengths and what could be improved
- formative assessments do not count towards actual assessment, it's just official feedback
- 100 lines for both week 4 and week 5 expected

### - Presentation of the current app to the TA (0 min) --> dark theme, html rendering (+errors)

- product isn't shippable yet

## - Talking points:

#### - < Discuss the key features document (basic requirements for a 6, and extensions for higher) (2 min)

- We have to decide till when we will fulfill the basic requirements
- We will focus on the basic requirements first

#### - < Check-in on tasks for the week, what is everyone working on? (10 minutes, see below)

#### - < Fetching from the database and display (3 min)
#### - < Creating tests for MainController and MarkdownCtrl (3 min)
#### - < Rendering the note (Webview panel and render button)
#### - < Searching algorithm (3 min)
#### - < Undo functionality (creation, deletion, renaming) (3min)
#### - < Keyboard Shortcuts (3min)
### - Summarize action points: Who, what, when? (3 min)

- Serban: will finish changing title, as of now if we close the app the notes aren't saved, the connection will be done in the lab
- Lukas: will review Yoanna's request
- Radu: did a lot for the markdown rendering, there is a problem for the escaping sequences, could look at issues or do tests
- Vidhi and Serban: keyboard shortcuts are part of the grade, but not deciding for passing,
- will be good to use Postman, could check using SQL queries
- Michal: an addition would be to make the pipeline fail
- TA: Serban did the whole backend, everyone should be working on both, 
- another person to help him would be good - on  multicollection
- gitlab: checkout Serban's checklist - don't forget to add time estimate and delete branches 

### - Question round, anything to add before closing? (0 min)

- no questions 

### - Feedback round (2 min)

- Lukas: Vidhi did a great job being a chair 

### - Closure (1 min)