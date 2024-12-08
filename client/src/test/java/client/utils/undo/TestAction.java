package client.utils.undo;

import java.util.List;

public record TestAction(List<String> data, String addedString, boolean cannotUndo, boolean cannotRedo)
        implements ClientAction {

    public TestAction(List<String> data, String addedString) {
        this(data, addedString, false, false);
    }

    @Override
    public void undo() throws CannotUndoException {
        if (cannotUndo) throw new CannotUndoException();
        data.removeLast();
    }

    @Override
    public void redo() throws CannotRedoException {
        if (cannotRedo) throw new CannotRedoException();
        data.add(addedString);
    }
}
