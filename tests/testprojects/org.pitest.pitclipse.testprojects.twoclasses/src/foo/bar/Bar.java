package foo.bar;

public class Bar {

    public int f(int i) {
        java.util.ArrayList<Object> pointless = new java.util.ArrayList<>();
        if (pointless.size() == 1)
            return i + 1;
        else
            return 0;
    }

}
