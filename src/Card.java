public class Card {
    private int id;
    private double balance;
    public Card(int id) {
        this.id = id;
        this.balance = 0.0;
    }
    public int getId() {
        return id;
    }
    public double getBalance() {
        return balance;
    }
    public void deposit(double amount) {
        balance += amount;
    }
    public boolean withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        } else {
            return false;
        }
    }
}