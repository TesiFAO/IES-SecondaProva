package it.unimarconi.ies;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simone Murzilli
 */
public class IES {

    //private static final BigInteger X0 = new BigInteger("1111");
    private static final BigInteger X0 = new BigInteger("2221");
    private static final BigInteger A = new BigInteger("1220703125");
    private static final BigInteger M = new BigInteger("2147483648");
    private static final double LAMBDA = 0.00785;
    private static final double N0 = 60.0; //N0
    private static final int N_PRERUN_FAILURES = 100; // n=100 failure
    private static final double A_MUSA = 0.95;
    private static final int N_RUN_FAILURES = 50; // N

    public static void main(String[] args) {

        // generaRi
        List<BigDecimal> Ri = generaRi();

        // pre-run
        System.out.println("PRE-RUN");
        List<Double> preRun = calcolaPreRun(Ri); // tempi di interfailure
        Double totale_prerun_tau = 0.0;
        for (Double v : preRun)
            totale_prerun_tau += v;
        System.out.println("Tempi di interfailure T: " + preRun); // T1, T100
        System.out.println("Tempo totale di pre-run: " + totale_prerun_tau);

        Double fi_0 = N_PRERUN_FAILURES / (totale_prerun_tau * N0);
        System.out.println("Fi(0): " + fi_0); //tasso di failure per difetto

        // run
        System.out.println("\nRUN");
        List<Double> run = calcolaRun(Ri, fi_0); // tempi di interfailure
        Double totale_run_tau = 0.0;
        //for (Double v : run)
        //    totale_run_tau += v;
        System.out.println("Tempi interfailure T: " + run);
    }

    /**
     * Calcola Pre-Run
     * @param Ri lista Ri
     * @return Lista di inter-failure
     */
    private static List<Double> calcolaPreRun(List<BigDecimal> Ri) {
        List<Double> l = new ArrayList<Double>();
        for (int i = 0; i < N_PRERUN_FAILURES; i++) {
            l.add(-1.0 * ( 1 / LAMBDA ) * (Math.log(Ri.get(i).doubleValue())));
        }
        return l;
    }

    /**
     * Calcola Run
     * @param Ri   lista Ri
     * @param fi_0 parametro fi_0
     * @return Lista di inter-failure
     */
    private static List<Double> calcolaRun(List<BigDecimal> Ri, Double fi_0) {
        List<Double> l= new ArrayList<Double>();
        Double lambda_i;
        Double totale_run_tau = 0.0;
        for (int i = 0; i < N_RUN_FAILURES; i++) {
            lambda_i = fi_0 * N0 * Math.exp(- 1.0 * fi_0 * A_MUSA * totale_run_tau);
            double v = -1.0 * (Math.log(Ri.get(i + N_PRERUN_FAILURES).doubleValue())) / lambda_i;
            totale_run_tau += v;
            l.add(v);
        }
        return l;
    }

    /**
     * Genera Ri
     * @return Ritorna una lista di Ri
     */
    private static List<BigDecimal> generaRi() {
        List<BigDecimal> Ri = new ArrayList<BigDecimal>();
        // genera x0
        BigInteger X = (A.multiply(X0)).mod(M).mod(M);

        // genera Ri
        Ri.add(new BigDecimal(X).divide(new BigDecimal(M)));
        int totale = N_PRERUN_FAILURES + N_RUN_FAILURES;
        for (int i = 1; i < totale; i++) {
            X = A.multiply(X).mod(M);
            Ri.add(new BigDecimal(X).divide(new BigDecimal(M)));
        }
        return Ri;
    }
}
