| Key          | Value                                                                        |
|--------------|------------------------------------------------------------------------------|
| Date         | 22.01.2025                                                                   |
| Time         | 13:45                                                                        |
| Location     | DW PC1 Cubicle 6                                                             |
| Chair        | Untu Șerban                                                                  |
| Minute Taker | Lukasz Kasprzak                                                              |
| Attendees    | Yoanna Dyakova <br/> Malmare Radu <br/> Vidhi Khakhar <br/> Michał Barciński |

# Agenda Items

## Opening by chair, check-in (1 min)
Hello, everybody is present, everybody is ready for the meeting.

## How is everybody doing, quick round among team members (2 min)
Michal : happy and ready to work.
Lucas: happy but not ready to work.  
Joanna : happy and ready to work.
Vidhi: happy but not ready to work.
Radu: Did calculus, and ready to work.  
Serban: focused on finishing the project.

## Anything to add to the agenda? (2 min)
Radu: change in agenda: Basic requirements - do we need manual refresh button to take into account collections and files?

## Any announcements by the team (what has everybody been working on?) (2 min)

What we have worked during the past week:
Serban: Network menu, everything on all servers can be seen. Cycling through shortcuts.
Michal: Tests for CollectionController, moving notes to different collection.
Lucas: Default collection functionality, setting collections to act as default.
Yoanna: Creating notes in specific collection, tests for EmbeddedFiles.
Vidhi: Embedded file tests, added shortcut descriptions in README.
Radu: URL encoding and tests for embedded file repository to get the 100 lines in the server.

## Any announcements by the TA (4 min)
- remember about the Buddycheck next friday.
- fill in the course evaluation.
- server requirement for multicollection is dropped (extra freature at this point).
- do final product presentation today.
- read the final knockout criteria to make sure you pass the course.

## Presentation of the current app to the TA (play-testing multiple servers) (10 min)
- Test the multicollection ability and present it to the TA. (it worked, approved by TA)
- Language support works fully (TA approved).
- notes should be added to the collection youre currently in (TA approved).
- Vidhi's shortcut is working (TA approved).
- Default functionality is working (TA approved).

## Talking points (15 min)

### Do we meet all the accessibility requirements? (6 min)
- Vidhi takes over Yoanna's issue with high contrast UI.
- all necessery issues have been created, most of them are assigned.
- Undo deletion - should stay (TA advised so as well).
- Document in README all the undo's.
- Write success messages (docuemnt them as well).
- unavailability of a netnode server - we have "server not available text", it's enough.

### Are we going to add TestFX to test the frontend? Did we test everything else? (2 min)
- Line coverage right now: bad
- Serban wants to create the issue and set the priority to low. If there is still time at the end of the week he will do it.

### What can we improve with the current UI, so the users have a more polished experience? (5 min)
- this is the time to make last minute amendments to the UI to improve experience.
- Michal suggested: remove filter bitax.
- TA suggested: add hover effects, for example a button descriptions when hovered on.
- Edit collections menu - input elements should be removed(already created collection should have the server adress's change disabled).

### Does everyone have something to work on? Will everyone meet the knockout criteria? (2 min)
Yes
### Discuss menu refresh button (issue #101) (2 min)
We need to take into account colelctions and embedded files while refreshing.

## Summarize action points: Who, what, when? (6 min)
- Radu: Test repo issue. #102 issue also.
- Vidhi: fix the files UI.
- Lucas: Add "all"(hopefully today), issue and fix bugs
- Michal: Fix bug with adding and deleting a Note.
- Serban: Undo for adding files and moving notes to different collection. README, and issue #88.
- Yoanna: add repo usage to the server, click to download functionality. Issues #84 and #86.

Please focus on fixing the bugs as a priotity with a few high-priority issues.

## Feedback round (1 min)
Group: Happy to announce Serban as a chariman was very good.
TA: Please try to do as little FX tests, and other hazardous tests, as possible toward the end of the week not to fail the pipeline.

## Closure (30 sec)
It was pleasure to do CSEP with you.
