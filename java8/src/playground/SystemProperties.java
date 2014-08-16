package playground;

public class SystemProperties {

    public static void main(String[] args) {
        System.getProperties()
                .forEach((key, value) ->
                        System.out.printf("%38s : %-38s%n", key, value)
        );
    }

}
