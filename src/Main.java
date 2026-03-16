import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    DNSEntry(String domain, String ipAddress, int ttl) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + ttl * 1000;
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {

    private final int MAX_SIZE = 100;
    private LinkedHashMap<String, DNSEntry> cache;
    private int hits = 0;
    private int misses = 0;

    DNSCache() {
        cache = new LinkedHashMap<String, DNSEntry>(16, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > MAX_SIZE;
            }
        };
    }

    public synchronized String resolve(String domain) {

        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                return "Cache HIT → " + entry.ipAddress;
            } else {
                cache.remove(domain);
            }
        }

        misses++;
        String newIP = queryUpstreamDNS(domain);
        cache.put(domain, new DNSEntry(domain, newIP, 300));

        return "Cache MISS → Query upstream → " + newIP;
    }

    private String queryUpstreamDNS(String domain) {
        Random r = new Random();
        return "172.217.14." + (200 + r.nextInt(50));
    }

    public void cleanExpiredEntries() {
        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, DNSEntry> e = it.next();
            if (e.getValue().isExpired()) {
                it.remove();
            }
        }
    }

    public String getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);
        return "Hit Rate: " + String.format("%.2f", hitRate) + "%";
    }

    public static void main(String[] args) {

        DNSCache dns = new DNSCache();

        System.out.println(dns.resolve("google.com"));
        System.out.println(dns.resolve("google.com"));

        dns.cleanExpiredEntries();

        System.out.println(dns.getCacheStats());
    }
}