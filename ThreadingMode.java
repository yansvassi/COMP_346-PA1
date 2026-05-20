public enum ThreadingMode {
    BUSY_WAIT,      // Spin loop (wastes CPU)
    YIELD,          // Thread.yield() (friendly)
}
