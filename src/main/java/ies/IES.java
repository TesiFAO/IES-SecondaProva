package ies;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simone Murzilli
 */
public class IES {
    private static final BigInteger X0 = new BigInteger("3673");
    //private static final BigInteger X0 = new BigInteger("2221");
    private static final BigInteger A = new BigInteger("1220703125");
    private static final BigInteger M = new BigInteger("2147483648");
    private static final double LAMBDA = 0.00785;
    private static final double N0 = 60.0;
    private static final int N_PRERUN_FAILURES = 100;
    private static final double A_MUSA = 0.95;
    private static final int N_RUN_FAILURES = 50;

    public static void main(String[] args) {

        // genera rn
        List<BigDecimal> rn = generaRn();

        // pre-run
        System.out.println("PRE-RUN");
        List<Double> preRun = calcolaPreRun(rn);
        Double totale_prerun_tau = 0.0;
        for (Double v : preRun)
            totale_prerun_tau += v;
        System.out.println("Tempi di interfailure T: " + preRun);
        System.out.println("Tempo totale di pre-run: " + totale_prerun_tau);

        Double fi_0 = N_PRERUN_FAILURES / (totale_prerun_tau * N0);
        System.out.println("Fi(0): " + fi_0);

        // run
        System.out.println("\nRUN");
        List<Double> run = calcolaRun(rn, fi_0);
        System.out.println("Tempi interfailure T: {" + print(run) + "}");
    }

    /**
     * Calcola Pre-Run
     * @param rn lista rn
     * @return Lista di inter-failure
     */
    private static List<Double> calcolaPreRun(List<BigDecimal> rn) {
        List<Double> l = new ArrayList<Double>();
        for (int i = 0; i < N_PRERUN_FAILURES; i++) {
            l.add(-1.0 * ( 1 / LAMBDA ) * (Math.log(rn.get(i).doubleValue())));
        }
        return l;
    }

    /**
     * Calcola Run
     * @param rn   lista rn
     * @param fi_0 parametro fi_0
     * @return Lista di inter-failure
     */
    private static List<Double> calcolaRun(List<BigDecimal> rn, Double fi_0) {
        List<Double> l= new ArrayList<Double>();
        Double lambda_i;
        Double totale_run_tau = 0.0;
        for (int i = 0; i < N_RUN_FAILURES; i++) {
            lambda_i = fi_0 * N0 * Math.exp(- 1.0 * fi_0 * A_MUSA * totale_run_tau);
            double v = -1.0 * (Math.log(rn.get(i + N_PRERUN_FAILURES).doubleValue())) / lambda_i;
            totale_run_tau += v;
            l.add(v);
        }
        return l;
    }

    /**
     * Genera rn
     * @return Ritorna una lista di rn
     */
    private static List<BigDecimal> generaRn() {
        List<BigDecimal> rn = new ArrayList<BigDecimal>();
        // genera x0
        BigInteger X = (A.multiply(X0)).mod(M).mod(M);

        // genera rn
        rn.add(new BigDecimal(X).divide(new BigDecimal(M)));
        int totale = N_PRERUN_FAILURES + N_RUN_FAILURES;
        for (int i = 1; i < totale; i++) {
            X = A.multiply(X).mod(M);
            rn.add(new BigDecimal(X).divide(new BigDecimal(M)));
        }
        return rn;
    }

    private static String print(List l) {
        String s = "";
        for(int i=0; i < l.size(); i++) {
            s += l.get(i);
            if ( i < l.size() -1 )
                s += (",");
        }
        return s;
    }
}
