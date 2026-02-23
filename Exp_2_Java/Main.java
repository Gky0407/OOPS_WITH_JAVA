import java.util.*;

class Student {
    private String uid;
    private String name;
    private int fineAmount;
    private int currentBorrowCount;

    public Student(String uid, String name, int fineAmount, int currentBorrowCount) {
        this.uid = uid;
        this.name = name;
        this.fineAmount = fineAmount;
        this.currentBorrowCount = currentBorrowCount;
    }

    public String getUid() { return uid; }
    public int getFineAmount() { return fineAmount; }
    public int getCurrentBorrowCount() { return currentBorrowCount; }
    public void incrementBorrowCount() { this.currentBorrowCount++; }
}

class Asset {
    private String assetId;
    private String assetName;
    private boolean available;
    private int securityLevel;

    public Asset(String assetId, String assetName, boolean available, int securityLevel) {
        this.assetId = assetId;
        this.assetName = assetName;
        this.available = available;
        this.securityLevel = securityLevel;
    }

    public String getAssetId() { return assetId; }
    public String getAssetName() { return assetName; }
    public boolean isAvailable() { return available; }
    public int getSecurityLevel() { return securityLevel; }
    public void setAvailable(boolean available) { this.available = available; }
}

class CheckoutRequest {
    private String uid;
    private String assetId;
    private int hoursRequested;

    public CheckoutRequest(String uid, String assetId, int hoursRequested) {
        if (hoursRequested < 1 || hoursRequested > 6) {
            throw new IllegalArgumentException("Hours must be between 1 and 6");
        }
        this.uid = uid;
        this.assetId = assetId;
        this.hoursRequested = hoursRequested;
    }

    public String getUid() { return uid; }
    public String getAssetId() { return assetId; }
    public int getHoursRequested() { return hoursRequested; }
    public void setHoursRequested(int hrs) { this.hoursRequested = hrs; }
}

class ValidationUtil {

    public static void validateUid(String uid) throws IllegalArgumentException {
        if (uid == null || uid.length() < 8 || uid.length() > 12 || uid.contains(" ")) {
            throw new IllegalArgumentException("Invalid UID format");
        }
    }

    public static void validateAssetId(String assetId) throws IllegalArgumentException {
        if (assetId == null || !assetId.matches("LAB-\\d+")) {
            throw new IllegalArgumentException("Invalid Asset ID format");
        }
    }

    public static void validateHours(int hrs) throws IllegalArgumentException {
        if (hrs < 1 || hrs > 6) {
            throw new IllegalArgumentException("Hours must be 1 to 6");
        }
    }
}


class AssetStore {
    private HashMap<String, Asset> assets = new HashMap<>();

    public void addAsset(Asset a) {
        assets.put(a.getAssetId(), a);
    }

    public Asset findAsset(String assetId) throws NullPointerException {
        Asset a = assets.get(assetId);
        if (a == null) {
            throw new NullPointerException("Asset not found: " + assetId);
        }
        return a;
    }

    public void markBorrowed(Asset a) throws IllegalStateException {
        if (!a.isAvailable()) {
            throw new IllegalStateException("Asset already borrowed");
        }
        a.setAvailable(false);
    }
}


class AuditLogger {
    public static void log(String msg) {
        System.out.println("[AUDIT] " + msg);
    }

    public static void logError(Exception e) {
        System.out.println("[ERROR] " + e.getMessage());
    }
}


class CheckoutService {

    private HashMap<String, Student> students;
    private AssetStore store;

    public CheckoutService(HashMap<String, Student> students, AssetStore store) {
        this.students = students;
        this.store = store;
    }

    public String checkout(CheckoutRequest req)
            throws IllegalArgumentException, IllegalStateException,
                   SecurityException, NullPointerException {


        ValidationUtil.validateUid(req.getUid());
        ValidationUtil.validateAssetId(req.getAssetId());
        ValidationUtil.validateHours(req.getHoursRequested());

        Student s = students.get(req.getUid());
        if (s == null) {
            throw new NullPointerException("Student not found: " + req.getUid());
        }

   
        if (s.getFineAmount() > 0) {
            throw new IllegalStateException("Outstanding fine exists");
        }

        if (s.getCurrentBorrowCount() >= 2) {
            throw new IllegalStateException("Borrow limit reached");
        }

  
        Asset a = store.findAsset(req.getAssetId());

        if (!a.isAvailable()) {
            throw new IllegalStateException("Asset not available");
        }

        if (a.getSecurityLevel() == 3 && !req.getUid().startsWith("KRG")) {
            throw new SecurityException("Restricted asset");
        }


        if (req.getHoursRequested() == 6) {
            System.out.println("Note: Max duration selected. Return strictly on time.");
        }

        if (a.getAssetName().contains("Cable") && req.getHoursRequested() > 3) {
            req.setHoursRequested(3);
            System.out.println("Policy applied: Cables can be issued max 3 hours. Updated to 3.");
        }


        store.markBorrowed(a);
        s.incrementBorrowCount();

        return "TXN-20260223-" + a.getAssetId() + "-" + s.getUid();
    }
}

public class Main {
    public static void main(String[] args) {

      
        HashMap<String, Student> students = new HashMap<>();
        students.put("KRG20281", new Student("KRG20281", "Gourav", 0, 1));
        students.put("ABC12345", new Student("ABC12345", "Rahul", 50, 0));
        students.put("KRG99999", new Student("KRG99999", "Aman", 0, 2));

     
        AssetStore store = new AssetStore();
        store.addAsset(new Asset("LAB-101", "HDMI Cable", true, 1));
        store.addAsset(new Asset("LAB-202", "Oscilloscope", true, 3));
        store.addAsset(new Asset("LAB-303", "Mouse", false, 1));

        CheckoutService service = new CheckoutService(students, store);

        
        CheckoutRequest[] requests = {
            new CheckoutRequest("KRG20281", "LAB-101", 5),
            new CheckoutRequest("KRG20281", "BAD-1", 2),   
            new CheckoutRequest("ABC12345", "LAB-202", 2) 
        };

        for (CheckoutRequest req : requests) {
            try {
                String receipt = service.checkout(req);
                System.out.println("SUCCESS: " + receipt);

            } catch (IllegalArgumentException e) {
                AuditLogger.logError(e);

            } catch (NullPointerException e) {
                AuditLogger.logError(e);

            } catch (SecurityException e) {
                AuditLogger.logError(e);

            } catch (IllegalStateException e) {
                AuditLogger.logError(e);

            } finally {
                AuditLogger.log("Attempt finished for UID=" +
                        req.getUid() + ", asset=" + req.getAssetId());
                System.out.println("--------------------------------");
            }
        }
    }
}