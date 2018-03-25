package edu.odu.cs.gold.repository;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import edu.odu.cs.gold.model.PermitType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class PermitTypeRepository {
    private HazelcastInstance hazelcastInstance;
    private String collectionName;

    public PermitTypeRepository(HazelcastInstance hazelcastInstance, String collectionName) {
        this.hazelcastInstance = hazelcastInstance;
        this.collectionName = collectionName;
    }

    public String getId(PermitType entity) {
        return entity.getPermitTypeKey();
    }

    public List<PermitType> findAll() {
        IMap map = hazelcastInstance.getMap(collectionName);
        return new ArrayList<>(map.values());
    }

    public PermitType findByKey(String key) {
        IMap map = hazelcastInstance.getMap(collectionName);
        return (PermitType)map.get(key);
    }

    public List<PermitType> findByKeys(Set<String> keys) {
        IMap map = hazelcastInstance.getMap(collectionName);
        return new ArrayList<>(map.getAll(keys).values());
    }

    public List<PermitType> findByPredicate(Predicate predicate) {
        IMap map = hazelcastInstance.getMap(collectionName);
        return new ArrayList<>(map.values(predicate));
    }

    public void save(PermitType entity) {
        IMap map = hazelcastInstance.getMap(collectionName);
        map.set(getId(entity), entity);
    }

    public void save(Collection<PermitType> entities) {
        IMap map = hazelcastInstance.getMap(collectionName);
        for (PermitType entity : entities) {
            map.set(getId(entity), entity);
        }
    }

    public void delete(String key) {
        IMap map = hazelcastInstance.getMap(collectionName);
        map.delete(key);
    }

    public int countByPredicate(Predicate predicate) {
        IMap map = hazelcastInstance.getMap(collectionName);
        return map.values(predicate).size();
    }

    public int deleteByPredicate(Predicate predicate) {
        IMap map = hazelcastInstance.getMap(collectionName);
        List<PermitType> entities = this.findByPredicate(predicate);
        for (PermitType entity : entities) {
            map.delete(getId(entity));
        }
        return entities.size();
    }

    public void loadAll() {
        IMap map = hazelcastInstance.getMap(collectionName);
        map.loadAll(false);
    }
}
