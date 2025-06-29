package database.daoimpl;

import auth.UserRecord;
import database.dao.UserDAO;
import database.util.HibernateUtil;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class HibernateUserDAO implements UserDAO {

  @Override
  public boolean register(String username, String passwordHash, String salt) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      Transaction tx = session.beginTransaction();
      User user = new User();
      user.setUsername(username);
      user.setPasswordHash(passwordHash);
      user.setSalt(salt);
      session.persist(user);
      tx.commit();
      return true;
    } catch (Exception e) {
      System.err.println("[REGISTER ERROR] " + e.getMessage());
      return false;
    }
  }

  @Override
  public boolean checkCredentials(String username, String passwordHash) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      Query<User> query =
          session.createQuery(
              "FROM User WHERE username = :username AND passwordHash = :hash", User.class);
      query.setParameter("username", username);
      query.setParameter("hash", passwordHash);
      return query.uniqueResult() != null;
    } catch (Exception e) {
      System.err.println("[CHECK ERROR] " + e.getMessage());
      return false;
    }
  }

  @Override
  public UserRecord getUserRecord(String username) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
      query.setParameter("username", username);
      User user = query.uniqueResult();
      if (user != null) {
        return new UserRecord(user.getId(), user.getPasswordHash(), user.getSalt());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String getUsername(int userId) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      User user = session.get(User.class, userId);
      return user != null ? user.getUsername() : null;
    } catch (Exception e) {
      System.err.println("[GET USERNAME ERROR] " + e.getMessage());
      return null;
    }
  }

  @Override
  public int getUserId(String username) {
    UserRecord user = getUserRecord(username);
    return user != null ? user.id() : -1;
  }
}
