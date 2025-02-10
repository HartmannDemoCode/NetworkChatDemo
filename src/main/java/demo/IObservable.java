package demo;

public interface IObservable {
    void addObserver(IObserver observer);
    void removeObserver(IObserver observer);
    void broadcast(String message);
}
