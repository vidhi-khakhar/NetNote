package client.utils.undo;

import java.util.List;

public class TestAction implements ClientAction {

    private final List<String> data;
    private final String addedString;

    public TestAction(List<String> data, String addedString) {
        this.data = data;
        this.addedString = addedString;
    }

    @Override
    public void undo() {
        data.removeLast();
    }

    @Override
    public void redo() {
        data.add(addedString);
    }
}
