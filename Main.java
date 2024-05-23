import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        
        ParkingGarage parkingGarage = new ParkingGarage(3, 2);
        ParkingSystem parkingSystem = new ParkingSystem(parkingGarage, 5);
        Driver driver1 = new Driver(1, new Car());
        Driver driver2 = new Driver(2, new Limo());
        Driver driver3 = new Driver(3, new Semi()); 

        System.out.println(parkingSystem.parkVehicle(driver1));
        System.out.println(parkingSystem.parkVehicle(driver2));
        System.out.println(parkingSystem.parkVehicle(driver3));

        System.out.println(parkingSystem.removeVehicle(driver1));
        System.out.println(parkingSystem.removeVehicle(driver2));
        System.out.println(parkingSystem.removeVehicle(driver3));
    }
}

//Begin with the vehicle class where we determine the size of the vehicle,
//and how many spots do they take. 

class Vehicle {
    private int spotSize; 

    public Vehicle(int spotSize){
        this.spotSize = spotSize;
    }
    public int getSpotSize(){
        return this.spotSize;
    }
}

//A Driver class now to determine which vehicle belongs to and total 
//payement due. also include method to charge the driver for parking.

class Driver {
    private int id;
    private Vehicle vehicle;
    private int paymentDue;

    public Driver(int id, Vehicle vehicle) {
        this.id = id;
        this.vehicle = vehicle;
        this.paymentDue = 0;
    }

    public Vehicle getVehicle(){
        return this.vehicle;
    }
    public int getId(){
        return this.id;
    }
    public void charge(int amount){
        this.paymentDue += amount;
    }
    public int getPaymentDue(){
        return paymentDue;
    }
}

//Class car will inherti vehilce class and each will have a predefined
//size for limo, semi, and normal. 

class Car extends Vehicle {
    public Car(){
        super(1);
    }
}
class Limo extends Vehicle {
    public Limo(){
        super(2);
    }
}
class Semi extends Vehicle {
    public Semi(){
        super(3);
    }
}

//class parking floor will have parking spots that are open or taken, 
//will also keep track of range of spots occupied by vehicle.

class ParkingFloor {
    private int[] spots; 
    private Map<Vehicle, int[]> vehicleMap;

    public ParkingFloor(int spotCount){
        this.spots = new int[spotCount];
        this.vehicleMap = new HashMap<>();
    }

    public boolean parkVehicle(Vehicle vehicle){
        int size = vehicle.getSpotSize();
        int l = 0, r = 0;
        while(r < this.spots.length){
            if(this.spots[r] != 0){
                l = r + 1;
            }
            if(r - l + 1 == size){
                for(int k=l; k <= r; k++){
                    this.spots[k] = 1;
                }
                this.vehicleMap.put(vehicle, new int[]{l,r});
                return true;
            }
            r++;
        }
        return false;
    }

    public void removeVehicle(Vehicle vehicle){
        int[] startEnd = this.vehicleMap.get(vehicle);
        int start = startEnd[0], end = startEnd[1];
        for(int i=start; i <= end; i++){
            this.spots[i] = 0;
        }
        this.vehicleMap.remove(vehicle);
    }

    public int[] getParkingSpots(){
        return this.spots;
    }

    public int[] getVehicleSpots(Vehicle vehicle){
        return this.vehicleMap.get(vehicle);
    }
}

// ParkingGarage class will have num of parkingfloor, 

class ParkingGarage {
    private ParkingFloor[] parkingFloors;

    public ParkingGarage(int floorCount, int spotsPerFloor){
        this.parkingFloors = new ParkingFloor[floorCount];
        for(int i=0; i<floorCount; i++){
            this.parkingFloors[i] = new ParkingFloor(spotsPerFloor);
        }
    }

    public boolean parkVehicle(Vehicle vehicle){
        for(ParkingFloor floor: this.parkingFloors){
            if(floor.parkVehicle(vehicle)){
                return true;
            }
        }
        return false;
    }

    public boolean removeVehicle(Vehicle vehicle){
        for(ParkingFloor floor: this.parkingFloors){
            if(floor.getVehicleSpots(vehicle) != null){
                floor.removeVehicle(vehicle);
                return true;
            }
        }
        return false;
    }
}

//Parking system class will br controller of garage. it will track, 
// parking hours, and payement charged to drivers.

class ParkingSystem {
    private ParkingGarage parkingGarage;
    private int hourlyRate;
    private Map<Integer, Integer> timeParked; // map driverId to time they parked. 

    public ParkingSystem(ParkingGarage parkingGarage, int hourlyRate){
        this.parkingGarage = parkingGarage;
        this.hourlyRate = hourlyRate;
        this.timeParked = new HashMap<>();
    }

    public boolean parkVehicle(Driver driver){
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        boolean isParked = this.parkingGarage.parkVehicle(driver.getVehicle());
        if(isParked) {
            this.timeParked.put(driver.getId(), currentHour);
        }
        return isParked;
    }

    public boolean removeVehicle(Driver driver){
        if(!this.timeParked.containsKey(driver.getId())){
            return false;
        }
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int timeParked = (int) Math.ceil(currentHour - this.timeParked.get(driver.getId()));
        driver.charge(timeParked * this.hourlyRate);
        this.timeParked.remove(driver.getId());

        return this.parkingGarage.removeVehicle(driver.getVehicle());
    }
}

