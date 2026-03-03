package model.protocol.Queries;

import model.protocol.Query;

public final class StopQuery extends Query {
    public static final String ACTION = "stop";
    @Override
    protected String getAction() { return ACTION; }

    public void fillAnswer() {
        super.setAnswer();
    }
}
