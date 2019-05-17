package dc.danh.sofuser.controller;

public class EnumManager {
    public enum ViewType {
        All(1), Bookmarked(2);

        private int value;

        ViewType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
