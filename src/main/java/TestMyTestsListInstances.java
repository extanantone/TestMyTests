import com.example.CalculadoraMetrosCuadrados.testmytests.TestMyTests;

public class TestMyTestsListInstances {
    public static void main(String[] args) {
        for(String key : TestMyTests.TEST_INSTANCES.keySet() ) {
            System.out.println(key);
        }
    }
}
