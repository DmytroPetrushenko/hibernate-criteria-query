package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert a phone " + phone
                    + "in a DB!", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            Set<Map.Entry<String, String[]>> entrySet = params.entrySet();
            Predicate predicateMaker = cb.conjunction();
            Predicate predicateModel = cb.conjunction();
            Predicate predicateCountryManufactured = cb.conjunction();
            Predicate predicateColor = cb.conjunction();
            for (Map.Entry<String, String[]> parameter : entrySet) {
                if (Objects.equals(parameter.getKey(), "maker")) {
                    predicateMaker = cb.and(predicateMaker, phoneRoot.get(parameter.getKey())
                            .in(parameter.getValue()));
                }
                if (Objects.equals(parameter.getKey(), "model")) {
                    predicateModel = cb.and(predicateModel, phoneRoot.get(parameter.getKey())
                            .in(parameter.getValue()));
                }
                if (Objects.equals(parameter.getKey(), "countryManufactured")) {
                    predicateCountryManufactured = cb.and(predicateCountryManufactured, phoneRoot
                            .get(parameter.getKey()).in(parameter.getValue()));
                }
                if (Objects.equals(parameter.getKey(), "color")) {
                    predicateColor = cb.and(predicateColor, phoneRoot.get(parameter.getKey())
                            .in(parameter.getValue()));
                }
            }
            query.where(cb.and(predicateMaker, predicateModel, predicateCountryManufactured,
                    predicateColor));
            return session.createQuery(query).getResultList();
        }
    }
}
