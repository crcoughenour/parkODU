package edu.odu.cs.gold.configuration;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import edu.odu.cs.gold.model.*;
import edu.odu.cs.gold.mongo.MongoMapStore;
import edu.odu.cs.gold.repository.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@PropertySources(value = {
        @PropertySource(value = "file:${propertiesDirectory}/parkodu.properties")
})
public class GoldConfiguration implements ApplicationContextAware {

    public static final String COLLECTION_GARAGE = "Garage";
    public static final String COLLECTION_FLOOR = "Floor";
    public static final String COLLECTION_PARKING_SPACE = "ParkingSpace";
    public static final String COLLECTION_BUILDING = "Building";

    public static final String COLLECTION_TRAVEL_DISTANCE_DURATION = "TravelDistanceDuration";

    public static final String COLLECTION_FLOOR_STATISTIC = "FloorStatistic";

    public static final String COLLECTION_USER = "User";

    @Autowired
    public Environment environment;

    @Autowired
    public MongoTemplate mongoTemplate;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    @Lazy
    public Config hazelcastConfig() {
        Config hazelcastConfig = new Config();

        // Add MapConfigs
        for (MapConfig mapConfig : applicationContext.getBeansOfType(MapConfig.class).values()) {
            hazelcastConfig.addMapConfig(mapConfig);
        }

        // Management Center Configs
        if (environment.getProperty("hazelcast.mancenter.enable", Boolean.class, false)) {
            String mancenterUrl = environment.getProperty("hazelcast.mancenter.url", "http://localhost:8080/mancenter");
            ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig();
            managementCenterConfig.setUrl(mancenterUrl);
            managementCenterConfig.setEnabled(true);
            hazelcastConfig.setManagementCenterConfig(managementCenterConfig);
        }

        // Group Configs
        hazelcastConfig.getGroupConfig().setName(environment.getProperty("hazelcast.group.name", "dev"));
        hazelcastConfig.getGroupConfig().setPassword(environment.getProperty("hazelcast.group.password", ""));

        return hazelcastConfig;
    }

    @Bean
    @Lazy
    public HazelcastInstance hazelcastInstance() {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig());
        return hazelcastInstance;
    }

    @Bean
    public GarageRepository garageRepository() {
        return new GarageRepository(hazelcastInstance(), COLLECTION_GARAGE);
    }

    @Bean
    public FloorRepository floorRepository() {
        return new FloorRepository(hazelcastInstance(), COLLECTION_FLOOR);
    }

    @Bean
    public ParkingSpaceRepository parkingSpaceRepository() {
        return new ParkingSpaceRepository(hazelcastInstance(), COLLECTION_PARKING_SPACE);
    }

    @Bean
    public BuildingRepository buildingRepository() {
        return new BuildingRepository(hazelcastInstance(), COLLECTION_BUILDING);
    }

    @Bean
    public TravelDistanceDurationRepository travelDistanceDurationRepository() {
        return new TravelDistanceDurationRepository(hazelcastInstance(), COLLECTION_TRAVEL_DISTANCE_DURATION);
    }

    @Bean
    public FloorStatisticRepository floorStatisticRepository() {
        return new FloorStatisticRepository(hazelcastInstance(), COLLECTION_FLOOR_STATISTIC);
    }

    @Bean
    public UserRepository userRepository() {
        return new UserRepository(hazelcastInstance(), COLLECTION_USER);
    }

    @Bean
    public MongoMapStore garageMapStore() {
        return new MongoMapStore(mongoTemplate, COLLECTION_GARAGE, Garage.class);
    }

    @Bean
    public MongoMapStore floorMapStore() {
        return new MongoMapStore(mongoTemplate, COLLECTION_FLOOR, Floor.class);
    }

    @Bean
    public MongoMapStore parkingSpaceMapStore() {
        return new MongoMapStore(mongoTemplate, COLLECTION_PARKING_SPACE, ParkingSpace.class);
    }

    @Bean
    public MongoMapStore buildingMapStore() {
        return new MongoMapStore(mongoTemplate, COLLECTION_BUILDING, Building.class);
    }

    @Bean
    public MongoMapStore travelDistanceDurationMapStore() {
        return new MongoMapStore(mongoTemplate, COLLECTION_TRAVEL_DISTANCE_DURATION, TravelDistanceDuration.class);
    }

    @Bean
    public MongoMapStore floorStatisticMapStore() {
        return new MongoMapStore(mongoTemplate, COLLECTION_FLOOR_STATISTIC, FloorStatistic.class);
    }

    @Bean
    public MongoMapStore userMapStore() {
        return new MongoMapStore(mongoTemplate, COLLECTION_USER, User.class);
    }

    @Bean
    public MapConfig garageRepositoryMapConfig() {
        MapConfig mapConfig = new MapConfig(COLLECTION_GARAGE);

        // MapStore
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setImplementation(garageMapStore());
        mapStoreConfig.setEnabled(true);
        mapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.EAGER);
        mapConfig.setMapStoreConfig(mapStoreConfig);

        return mapConfig;
    }


    @Bean
    public MapConfig floorRepositoryMapConfig() {
        MapConfig mapConfig = new MapConfig(COLLECTION_FLOOR);

        // MapStore
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setImplementation(floorMapStore());
        mapStoreConfig.setEnabled(true);
        mapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.EAGER);
        mapConfig.setMapStoreConfig(mapStoreConfig);

        // Indexed Attributes
        mapConfig.addMapIndexConfig(new MapIndexConfig("garageKey", false));
        mapConfig.addMapIndexConfig(new MapIndexConfig("number", false));

        return mapConfig;
    }

    @Bean
    public MapConfig parkingSpaceRepositoryMapConfig() {
        MapConfig mapConfig = new MapConfig(COLLECTION_PARKING_SPACE);

        // MapStore
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setImplementation(parkingSpaceMapStore());
        mapStoreConfig.setEnabled(true);
        mapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.EAGER);
        mapConfig.setMapStoreConfig(mapStoreConfig);

        // Indexed Attributes
        mapConfig.addMapIndexConfig(new MapIndexConfig("garageKey", false));
        mapConfig.addMapIndexConfig(new MapIndexConfig("available", false));
        mapConfig.addMapIndexConfig(new MapIndexConfig("number", false));

        return mapConfig;
    }

    @Bean
    public MapConfig buildingRepositoryMapConfig() {
        MapConfig mapConfig = new MapConfig(COLLECTION_BUILDING);

        // MapStore
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setImplementation(buildingMapStore());
        mapStoreConfig.setEnabled(true);
        mapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.EAGER);
        mapConfig.setMapStoreConfig(mapStoreConfig);

        // Indexed Attributes
        mapConfig.addMapIndexConfig(new MapIndexConfig("buildingKey", false));

        return mapConfig;
    }

    @Bean
    public MapConfig travelDistanceDurationRepositoryMapConfig() {
        MapConfig mapConfig = new MapConfig(COLLECTION_TRAVEL_DISTANCE_DURATION);

        // MapStore
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setImplementation(travelDistanceDurationMapStore());
        mapStoreConfig.setEnabled(true);
        mapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.EAGER);
        mapConfig.setMapStoreConfig(mapStoreConfig);

        // Indexed Attributes
        mapConfig.addMapIndexConfig(new MapIndexConfig("garageKey", false));
        mapConfig.addMapIndexConfig(new MapIndexConfig("buildingKey", false));
        mapConfig.addMapIndexConfig(new MapIndexConfig("travelMode", false));

        return mapConfig;
    }

    @Bean
    public MapConfig floorStatisticRepositoryMapConfig() {
        MapConfig mapConfig = new MapConfig(COLLECTION_FLOOR_STATISTIC);

        // MapStore
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setImplementation(floorStatisticMapStore());
        mapStoreConfig.setEnabled(true);
        mapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.EAGER);
        mapConfig.setMapStoreConfig(mapStoreConfig);

        // Indexed Attributes
        mapConfig.addMapIndexConfig(new MapIndexConfig("floorKey", false));

        return mapConfig;
    }

    @Bean
    public MapConfig userRepositoryMapConfig() {
        MapConfig mapConfig = new MapConfig(COLLECTION_USER);

        // MapStore
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setImplementation(userMapStore());
        mapStoreConfig.setEnabled(true);
        mapStoreConfig.setInitialLoadMode(MapStoreConfig.InitialLoadMode.EAGER);
        mapConfig.setMapStoreConfig(mapStoreConfig);

        // Indexed Attributes
        mapConfig.addMapIndexConfig(new MapIndexConfig("email", false));
        mapConfig.addMapIndexConfig(new MapIndexConfig("userName", false));
        mapConfig.addMapIndexConfig(new MapIndexConfig("confirmationToken", false));
        mapConfig.addMapIndexConfig(new MapIndexConfig("id",false));

        return mapConfig;
    }
}