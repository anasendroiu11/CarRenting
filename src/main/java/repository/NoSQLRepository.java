package repository;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import domain.Car;
import domain.Entity;
import domain.Rental;
import org.bson.Document;

import java.util.ArrayList;

public class NoSQLRepository<T extends Entity> implements InterfaceRepository<T> {
    private final MongoCollection<Document> collection;

    public NoSQLRepository(String connectionString, String dbName, String collectionName) {
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase(dbName);
        this.collection = database.getCollection(collectionName);
    }

    @Override
    public void add(T element) {
        Document doc = toDocument(element);
        collection.insertOne(doc);
    }

    @Override
    public void update(T element) {
        Document doc = toDocument(element);
        collection.replaceOne(Filters.eq("id", element.getId()), doc);
    }

    @Override
    public void delete(int id) {
        collection.deleteOne(Filters.eq("id", id));
    }

    @Override
    public boolean exists(int id) {
        return collection.find(Filters.eq("id", id)).first() != null;
    }

    @Override
    public T getById(int id) {
        Document doc = collection.find(Filters.eq("id", id)).first();
        if (doc == null) return null;
        return fromDocument(doc);
    }

    @Override
    public ArrayList<T> getAll() {
        ArrayList<T> list = new ArrayList<>();
        for (Document doc : collection.find()) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    @Override
    public int size() {
        return (int) collection.countDocuments();
    }

    @Override
    public void clear() {
        collection.deleteMany(new Document());
    }

    private Document toDocument(T entity) {
        if (entity instanceof Car car) {
            return new Document("id", car.getId())
                    .append("brand", car.getBrand())
                    .append("model", car.getModel());
        } else if (entity instanceof Rental rental) {
            Document carDoc = new Document("id", rental.getCar().getId())
                    .append("brand", rental.getCar().getBrand())
                    .append("model", rental.getCar().getModel());

            return new Document("id", rental.getId())
                    .append("car", carDoc)
                    .append("startDate", rental.getStartDate())
                    .append("endDate", rental.getEndDate());
        }
        throw new IllegalArgumentException("Unknown entity type");
    }

    @SuppressWarnings("unchecked")
    private T fromDocument(Document doc) {
        if (doc.containsKey("brand") && !doc.containsKey("car")) {
            int id = doc.getInteger("id");
            String brand = doc.getString("brand");
            String model = doc.getString("model");
            return (T) new Car(id, brand, model);
        } else if (doc.containsKey("car")) {
            int id = doc.getInteger("id");
            String start = doc.getString("startDate");
            String end = doc.getString("endDate");

            Document carDoc = (Document) doc.get("car");
            Car car = new Car(
                    carDoc.getInteger("id"),
                    carDoc.getString("brand"),
                    carDoc.getString("model")
            );
            return (T) new Rental(id, car, start, end);
        }
        return null;
    }
}