package jpql;

import javax.persistence.*;

public class JpqlMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            //TypedQuery : 반환 타입이 명확할 때 사용
//            TypedQuery<Member> typedQuery1 = em.createQuery("select m from Member m", Member.class);
//            TypedQuery<String> typedQuery2 = em.createQuery("select m.username from Member m", String.class);
            //Query : 반환 타입이 명확하지 않을 때 사용
//            Query query3 = em.createQuery("select m.username, m.age from Member m");

            //결과 조회 API
//            TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
//            //결과 조회 - 결과가 하나 이상일 때 getResultList()
//            List<Member> resultList = query.getResultList();
//            //결과가 없으면 빈 리스트 반환
//
//            //결과가 정확히 하나 일때 getSingleResult()
//            Member singleResult = query.getSingleResult();
//            //결과가 없으면 : javax.persistence.NoResultException
//            //결과가 둘 이상이면 : javax.persistence.NonUniqueResultException
//            System.out.println("singleResult = " + singleResult);


            //파라미터 바인딩 - 숫자는 지양, 문자 권장
            Member result = em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "member1")
                    .getSingleResult();
            System.out.println("result = " + result.getUsername());


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();

    }
}
