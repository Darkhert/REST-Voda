package cz.obergruber;

public class User {
    public String name;
    public String user_id;
    public String correctToken = "";
    public String playlist_id = "";
    public String refreshToken = "AQDoBEqWMytur2iZaZbHovVxn0k4KkyhCoIp1SS8_aPNGtzkC8BOtLWevqF6ttlV3-uHHZUz2Mtfe9NrcPToB8ANxnLkstk3WBctF2uPtESYtUL9CTv2bnC_uinEwviTwo8";

    public String expiredToken = "BQAhYuk2l9L8d5LYzQuTp0wz7TL29JDZvmsIFoNLbFPWfX4wZ0L4zAAP0YPj4S_RM1fsMdcOPVOlQxEdH-dLrOA-QKyzQl7gqcZ81WMKLIiJRfD6kKVRK1faT2njiFB37i9GlDKqUqVQufFZKE7USvaIon5YsNc0tlGFJqUqydZx5jOP-TSs_K1ehMjgG8e_TRb544-r3im1KLN77bcHY227p-cE86RosQA2xzHf_3yrf1OEh9fg6VqWldr27OqGdGKBZtCpdqAKlFTBosPn8RvIrA";
    public String invalidToken = "SK0TwsnCM04csZh02gprc9M4rWeGsBzb4EnWVJ9hciy9F9ADZ60CgpUQejnSZuhHc1LjIQO6Xnnav0rkWEjbJwulaH8g66VWEvFaXIr5MmLZxg1izKou38yJnLeqXWNbkZQM3zNcunFcEi4MDY9a9yiLqkMlHtOahrzvwinz3TONVXyfMsERosAH6BXC9wmXnypFxor6uykufLjhOO7YgXarvG9TvPGSmIp7XtwLHiWkghVU49x9NILlLbaE2nNoVIfbWGt5YZ1XHb3k42TZVV1AM2";
    public String[] songs = new String[] {"spotify:track:4iV5W9uYEdYUVa79Axb7Rh", "spotify:track:1301WleyT98MSxVHPZCA6M"};

    public User(String name, String user_id, String playlist_id, String correctToken) {
        this.name = name;
        this.user_id = user_id;
        this.correctToken = correctToken;
        this.playlist_id = playlist_id;
    }
}
