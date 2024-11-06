public class CourierCredsGen {
    public static Courier getRandomCourier() {
        final String courierLogin = RandomStringUtils.randomAlphabetic(10);
        final String courierPassword = RandomStringUtils.randomAlphabetic(10);
        final String courierFirstName = RandomStringUtils.randomAlphabetic(10);

        return new Courier(courierLogin, courierPassword, courierFirstName);
    }
}
