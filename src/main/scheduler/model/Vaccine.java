package scheduler.model;

public class Vaccine {
    private final String vaccineName;

    private int availableDoses;

    private Vaccine(VaccineBuilder builder) {
        this.vaccineName = builder.vaccineName;
        this.availableDoses = builder.availableDoses;
    }

    // Getters
    public String getVaccineName() {
        return vaccineName;
    }

    public int getAvailableDoses() {
        return availableDoses;
    }

    // Increment the available doses
    public void increaseAvailableDoses(int num) {

    }

    @Override
    public String toString() {
        return "Vaccine{" +
                "VaccineName='" + vaccineName + '\'' +
                ", AvailableDoses=" + availableDoses +
                '}';
    }

    public static class VaccineBuilder {
        private final String vaccineName;

        private int availableDoses;

        public VaccineBuilder(String vaccineName) {
            this.vaccineName = vaccineName;
        }

        public VaccineBuilder availableDoses(int availableDoses) {
            this.availableDoses = availableDoses;
            return this;
        }

        public Vaccine build() {
            return new Vaccine(this);
        }
    }
}