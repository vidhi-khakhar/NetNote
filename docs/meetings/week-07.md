| Key          | Value                                               |
|--------------|-----------------------------------------------------|
| Date         | 08.01.2024                                          |
| Time         | 13:45                                               |
| Location     | DW PC1 Cubicle 6                                    |
| Chair        | Lukasz Kasprzak                                     |
| Minute Taker | Michał Barciński                                    |
| Attendees    | Serban Untu <br/> Radu Malmare <br/> Yoanna Dyakova |


# Agenda Items 

## Opening by chair, check-in (1 min)

## How is everybody doing, quick round among team members (2 min)

- Everybody is well-rested after the holidays and ready to work.


## Presentation of the current app to the TA. Does our app meet all the basic criteria (dark mode for example) ? (6min)

- Several issues in the app were found during the presentation that need to be fixed:  
    - The app shouldn't allow for notes with the same title.  
    - Recreating a note using shortcuts doesn't work.  
    - Markdown problems: descriptions should support different languages. 

## Discuss the feedback we received last time for Contributions & Reviews and what we should do more (2 min)

- Some team members have less than one commit per week. This needs to be improved.  
- Code reviews are alright, but there is still room for improvement:  
    - We should discuss code-related matters directly in the MR on GitLab.  
    - We should avoid discussing code on WhatsApp.    

## Talking points (15 min)

### Do we add highlithing to the search ? For exapmle, whether the keyword was found in a title or text of the Note ? (2 min)

- Highlighting is not required in the basic requirements.  
- We decided to implement it if time allows. 

### Have all tasks for MultiCollection been distributed, maybe some changes here ? (3 min)

- All tasks for MultiCollection have been distributed, except for one.  
- The last unassigned issue will be resolved by Lucas.  

### Do we have enough testing, do we care about it ? "Run with coverage" gives less then 30% (3 min)

- MainCtrl cannot be tested in its current state and needs refactoring.  
- Tests for some classes must be implemented in the same merge request as the class itself.  
- Testing accounts for 4% of the final grade.   

### How is the 4.3 Embedded Files epic going ? Is Radu going to do it all alone ? (3 min)

- Radu is working on it, and it is almost finished.  
- There are still front-end tasks for Embedded Files that need to be completed.  
- After finishing the MultiCollection tasks, the remaining Embedded Files tasks will be distributed among team members.  

### Does everybody comply with the current UI Vidhi build ? Discussing the UI. (4 min)

- The current design looks great, but some improvements are needed:  
    - We will add more colors to ensure high contrast and visual appeal.  
    - Buttons for Embedded Files need to be added.   

## Summarize action points: Who, what, when? (8 min)

- Everyone has been assigned specific issues:  
    - Serban: Work on the "Edit Collections" UI and shortcuts for collections.  
    - Yoanna: Create new notes in a specific collection.  
    - Radu: Test the Embedded Files.  
    - Vidhi: Create a bin collection for deleted notes.  
    - Michał: Implement moving a note to another collection.  
    - Lucas: Add a dropdown to the main view to select the current collection. 

- some announcements from the TA :
    - Vidhi failed the knockout criteria (less than 100 lines of code).  
    - Feedback on Implemented Features will be available next week.
    - We should implement buttons that have both text and image.

## Feedback round (1 min)

- The chair performance was great.
- The meeting started 2 minutes earlier as everybody was present.

## Closure (30 sec)
- The meeting was closed.
