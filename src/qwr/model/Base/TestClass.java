package qwr.model.Base;

public class TestClass extends BasicElement{

    public TestClass(int timer, String titul, int xstruc, int user) {
        super(timer, titul, xstruc, user);
    }

    @Override
    public boolean read(String[] words) {
        return false;
    }
}//TestClass
