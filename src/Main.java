import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class Main {
    static Comparator<Process> QueueComparator = new Process.SJFComparator();
    static PriorityQueue<Process> processQueue = new PriorityQueue<>(QueueComparator);
    static ArrayList<Process> completedProcesses = new ArrayList<>();
    static Process.StarvedPredicate starvedPredicate = new Process.StarvedPredicate(1_000);
    static int iterations = 1_000_000;
    static int initialProcesses = 0;
    static Random r = new Random();
    static int switchCount = -1;

    public static void generateProcesses(int t) {
        double gaussianSample = r.nextGaussian() * (iterations / 6.0) + (iterations / 2.0);

        if(gaussianSample < iterations && gaussianSample > 0 && t > gaussianSample) {
            for(int i = 0; i < r.nextInt(100); i++) {
                processQueue.add(Process.generateRandomProcess(t));
            }
        }
    }

    public static void main(String[] args) {
        for(int t = 0; t < initialProcesses; t++) {
            Process randomProcess = Process.generateRandomProcess(0);

            processQueue.add(randomProcess);
        }

        Process current = null;
        for(int t = 0; t < iterations; t++) {
            generateProcesses(t);

            Process next = processQueue.poll();
            if(next == null) {
                continue;
            }

            switchCount += (next.equals(current)) ? 0 : 1;
            current = next;

            boolean completed = current.advanceProcess(1, t);

            if(completed) {
                completedProcesses.add(current);
            } else {
                processQueue.add(current);
            }
        }

        int aggregateSumProcessLength = completedProcesses.stream()
                .map(Process::getCompleted)
                .mapToInt(Integer::intValue)
                .sum();

//        int aggregateSumBegunProcessLength = aggregateSumProcessLength + processQueue.stream()
//                .map(Process::getCompleted)
//                .filter(completed -> completed > 0)
//                .mapToInt(Integer::intValue)
//                .sum();

        double avgCompletion = completedProcesses.stream()
                .map(process -> process.getCompletionTime() - process.getStartTime())
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0);

        double avgWaitTime = completedProcesses.stream()
                .map(process -> process.getStartTime() - process.getProcessInitializationTime())
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0);

        long starvedProcesses = completedProcesses.stream()
                .filter(starvedPredicate)
                .count() + processQueue.stream()
                .filter(starvedPredicate)
                .count();

                System.out.printf("Average completion time for a process: %.2f%n" +
                "Average process wait time since initialization: %.2f%n" +
                "Sum of completed process lengths: %d%n" +
                //"Sum of begun process lengths (including completed): %d%n" +
                "Total processes served: %d%n" +
                "Total switch count: %d%n" +
                "Starved processes: %d",
                avgCompletion, avgWaitTime, aggregateSumProcessLength, /*aggregateSumBegunProcessLength,*/ completedProcesses.size(), switchCount, starvedProcesses);
    }
}