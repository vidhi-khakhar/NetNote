package client.utils.undo;

import java.util.*;

public class UndoHandler {

    private final int capacity;
    private int currentIndex;
    private final List<ClientAction> actions;

    /**
     * Constructor for the undo handler
     *
     * @param capacity The maximum number of actions that
     *                 can be undone at once.
     */
    public UndoHandler(int capacity) {
        this.capacity = capacity;
        this.actions = new ArrayList<>(capacity);
        this.currentIndex = -1;
    }

    /**
     * Pushes a client action to the top of the undo stack.<br>
     * <br>
     * This method should be called immediately after the action is performed
     * in the controller, for it to be tracked by the handler and be undoable.<br>
     * <br>
     * Doing this clears the rest of the stack, making you unable to redo any
     * actions that were previously undone.
     *
     * @param action The client action to be pushed to the top of the stack.
     */
    public void pushAction(ClientAction action) {
        if (this.currentIndex == this.capacity - 1) {
            this.actions.removeFirst();
            currentIndex--;
        }
        int numberActionsLeft = actions.size() - currentIndex - 1;
        for (int i = 1; i <= numberActionsLeft; i++) {
            actions.removeLast();
        }
        actions.add(action);
        currentIndex++;
    }

    /**
     * Undoes the most recent action that was not already undone.
     *
     * @throws CannotUndoException If there are no more actions to undo.
     */
    public void undoAction() throws CannotUndoException {
        if (currentIndex == -1) throw new CannotUndoException();
        try {
            actions.get(currentIndex).undo();
        } catch (CannotUndoException e) {
            actions.remove(currentIndex);
        }
        currentIndex--;
    }

    /**
     * Redoes the last action that was undone.
     *
     * @throws CannotRedoException If there are no more actions to redo.
     */
    public void redoAction() throws CannotRedoException {
        if (currentIndex == actions.size() - 1) throw new CannotRedoException();
        try {
            actions.get(currentIndex + 1).redo();
            currentIndex++;
        } catch (CannotRedoException e) {
            int numberActionsLeft = actions.size() - currentIndex - 1;
            for (int i = 1; i <= numberActionsLeft; i++) {
                actions.removeLast();
            }
        }
    }
}
