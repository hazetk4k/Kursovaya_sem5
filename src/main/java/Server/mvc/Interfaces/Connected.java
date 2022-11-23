package Server.mvc.Interfaces;

public interface Connected {
    public void addConnection(Connection connection);

    public void notifyConnections(String message, int flag);
}
