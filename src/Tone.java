public class Tone {

    // create a pure tone of the given frequency for the given duration
    public static double[] tone(double hz, double duration) {
        int n = (int) (StdAudio.SAMPLE_RATE * duration);
        double[] a = new double[n+1];
        for (int i = 0; i <= n; i++) {
            a[i] = Math.sin(2 * Math.PI * i * hz / StdAudio.SAMPLE_RATE);
        }
        return a;
    }

    public static void beep(double hz){

        // create the array
        double[] a = tone(hz, 0.05);

        // play it using standard audio
        StdAudio.play(a);
    }

}
