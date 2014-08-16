package playground;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created on 28.06.14 16:00.
 */
public class SourceFileWatcher implements Runnable {

    public static final PathMatcher FILE_PATTERN_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**/*.java");
    public static final String JAVA_HOME = System.getProperty("java.home");
    public static final String JAVAC = JAVA_HOME + "/../bin/javac";
    public static final String SOURCE_DIR = "src/main/java";
    public static final String TARGET_DIR = "target/classes";
    public static final String RUNNING_LOCK = "running.lock";
    public static final Path RUNNING_LOCK_FILE = Paths.get(SOURCE_DIR + "/" + RUNNING_LOCK);
//    public static final File LOG_FILE = new File("output.log");
//    public static final File ERROR_LOG_FILE = new File("error.log");
    private final WatchService watchService;
    private final ExecutorService executorService;
    private final BlockingQueue<Future<Result>> futures;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public SourceFileWatcher(final Path path) throws IOException, ExecutionException, InterruptedException {
        this.watchService = path.getFileSystem().newWatchService();
        this.executorService = Executors.newCachedThreadPool();
        Files.deleteIfExists(RUNNING_LOCK_FILE);
        Files.createFile(RUNNING_LOCK_FILE);
        futures = new LinkedBlockingQueue<>();

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.printf("Register for directory %s%n", dir);
                dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                return FileVisitResult.CONTINUE;
            }
        });
        executorService.execute(watchFutures());
        executorService.execute(this);
    }

    interface Result {
        String name();
        int code() throws InterruptedException;
        Boolean success() throws InterruptedException;
    }

    private Callable<Result> createTask(final Path file) {
        final String command = String.format("%1$s -sourcepath %2$s -classpath %3$s -d %3$s -g -verbose %4$s",
                JAVAC,
                SOURCE_DIR,
                TARGET_DIR,
                file.toString());
        return new Callable<Result>() {
            @Override
            public Result call() throws Exception {
//                final Process process = new ProcessBuilder(command)
//                        .redirectOutput(LOG_FILE)
//                        .redirectError(ERROR_LOG_FILE)
//                        .start();
                final Process process = Runtime.getRuntime().exec(command);
                return new Result() {
                    @Override
                    public String name() {
                        return file.getFileName().toString();
                    }

                    @Override
                    public int code() throws InterruptedException {
                        return process.waitFor();
                    }

                    @Override
                    public Boolean success() throws InterruptedException {
                        return code() == 0;
                    }
                };
            }
        };
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    switch (event.kind().name()) {
                        case "OVERFLOW" :
                            System.err.println("We lost Events");
                            break;
                        case "ENTRY_CREATE" :
                            System.out.printf("File %s is created", event.context());
                            if (event.kind().type().isAssignableFrom(Path.class)) {
                                Path file = getFullPath(key.watchable(), event);
                                if (FILE_PATTERN_MATCHER.matches(file)) {
                                    futures.put(executorService.submit(createTask(file)));
                                }
                            } else {
                                System.err.printf("Context is of type %s%n", event.kind().type());
                            }
                            System.out.println();
                            break;
                        case "ENTRY_MODIFY" :
                            System.out.printf("File %s is changed", event.context());
                            if (event.kind().type().isAssignableFrom(Path.class)) {
                                Path file = getFullPath(key.watchable(), event);
                                if (FILE_PATTERN_MATCHER.matches(file)) {
                                    futures.put(executorService.submit(createTask(file)));
                                }
                            } else {
                                System.err.printf("Context is of type %s%n", event.kind().type());
                            }
                            System.out.println();
                            break;
                        case "ENTRY_DELETE" :
                            System.out.printf("File %s is deleted", event.context());
                            if (event.kind().type().isAssignableFrom(Path.class)) {
                                if (((Path) event.context()).endsWith(RUNNING_LOCK)) {
                                    running.set(false);
                                    executorService.shutdown();
                                    System.out.println("!");
                                } else {
                                    System.out.println(".");
                                }
                            } else {
                                System.err.printf("Context is of type %s%n", event.kind().type());
                            }
                            break;
                        default:
                            System.out.printf("Event: %s%n", event.context());
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Path getFullPath(Watchable watchable, WatchEvent<?> event) {
        return ((Path) watchable).resolve((Path) event.context());
    }

    private Runnable watchFutures() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    while (running.get() || !futures.isEmpty()) {
                        Future<Result> future = futures.poll(1L, TimeUnit.SECONDS);
                        if (future != null) {
                            if (future.isDone()) {
                                Result result = future.get();
                                System.out.printf("Source %s compiled: %b (Code: %d)%n",
                                        result.name(),
                                        result.success(),
                                        result.code()
                                );
                            } else {
                                futures.put(future);
                            }
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static void main(String... args) throws Exception {
        new SourceFileWatcher(Paths.get(SOURCE_DIR).toAbsolutePath());
    }

}
