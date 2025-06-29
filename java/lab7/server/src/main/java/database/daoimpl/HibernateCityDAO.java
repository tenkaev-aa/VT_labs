package database.daoimpl;

import database.dao.CityDAO;
import database.util.HibernateUtil;
import java.util.Collection;
import java.util.List;
import model.City;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class HibernateCityDAO implements CityDAO {

  @Override
  public City insert(City city, int ownerId) {
    city.setOwnerId(ownerId);
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      Transaction tx = session.beginTransaction();
      session.persist(city);
      tx.commit();
      return city;
    } catch (Exception e) {
      System.err.println("[insert ERROR] " + e.getMessage());
      return null;
    }
  }

  @Override
  public boolean update(int id, City updatedCity, int ownerId) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      Transaction tx = session.beginTransaction();
      City existing = session.get(City.class, id);
      if (existing == null || existing.getOwnerId() != ownerId) return false;

      existing.setName(updatedCity.getName());
      existing.setCoordinates(updatedCity.getCoordinates());
      existing.setCreationDate(updatedCity.getCreationDate());
      existing.setArea(updatedCity.getArea());
      existing.setPopulation(updatedCity.getPopulation());
      existing.setMetersAboveSeaLevel(updatedCity.getMetersAboveSeaLevel());
      existing.setClimate(updatedCity.getClimate());
      existing.setGovernment(updatedCity.getGovernment());
      existing.setStandardOfLiving(updatedCity.getStandardOfLiving());
      existing.setGovernor(updatedCity.getGovernor());

      session.merge(existing);
      tx.commit();
      return true;
    } catch (Exception e) {
      System.err.println("[update ERROR] " + e.getMessage());
      return false;
    }
  }

  @Override
  public boolean delete(int id, int ownerId) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      Transaction tx = session.beginTransaction();
      City city = session.get(City.class, id);
      if (city != null && city.getOwnerId() == ownerId) {
        session.remove(city);
        tx.commit();
        return true;
      }
      return false;
    } catch (Exception e) {
      System.err.println("[delete ERROR] " + e.getMessage());
      return false;
    }
  }

  @Override
  public Collection<City> findAll() {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      return session.createQuery("FROM City", City.class).list();
    } catch (Exception e) {
      System.err.println("[findAll ERROR] " + e.getMessage());
      return List.of();
    }
  }

  @Override
  public City findById(int id) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      return session.get(City.class, id);
    } catch (Exception e) {
      System.err.println("[findById ERROR] " + e.getMessage());
      return null;
    }
  }

  @Override
  public boolean deleteAllByOwner(int ownerId) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      Transaction tx = session.beginTransaction();
      Query<?> q = session.createQuery("DELETE FROM City WHERE ownerId = :ownerId");
      q.setParameter("ownerId", ownerId);
      int affected = q.executeUpdate();
      tx.commit();
      return affected >= 0;
    } catch (Exception e) {
      System.err.println("[deleteAllByOwner ERROR] " + e.getMessage());
      return false;
    }
  }

  @Override
  public boolean deleteByIdsAndOwner(List<Integer> ids, int ownerId) {
    if (ids == null || ids.isEmpty()) return true;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      Transaction tx = session.beginTransaction();
      Query<?> q =
          session.createQuery("DELETE FROM City WHERE id IN (:ids) AND ownerId = :ownerId");
      q.setParameter("ids", ids);
      q.setParameter("ownerId", ownerId);
      int affected = q.executeUpdate();
      tx.commit();
      return affected >= 0;
    } catch (Exception e) {
      System.err.println("[deleteByIdsAndOwner ERROR] " + e.getMessage());
      return false;
    }
  }
}
