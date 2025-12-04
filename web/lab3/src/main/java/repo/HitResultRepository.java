package repo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import model.HitResult;

@ApplicationScoped
public class HitResultRepository {
  @PersistenceContext(unitName = "weblabPU")
  EntityManager em;

  @Transactional
  public HitResult save(HitResult h) {
    em.persist(h);
    return h;
  }

  public List<HitResult> findAllDesc(int limit) {
    return em.createQuery("select h from HitResult h order by h.created_at desc", HitResult.class)
        .setMaxResults(limit)
        .getResultList();
  }
}
