package jpql.function;

import jpql.Member;
import jpql.MemberType;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class FunctionMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member1 = new Member();
            member1.setUsername("관리자1");
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("관리자2");
            em.persist(member2);

            em.flush();
            em.clear();

            String queryConcat = "select concat('a', 'b') from Member m";
            List<String> concatResultList = em.createQuery(queryConcat, String.class)
                    .getResultList();
            for (String s : concatResultList) {
                System.out.println("s = " + s);
            }

            String querySubstring = "select substring(m.username, 2, 3) from Member m";
            List<String> subStringResultList = em.createQuery(querySubstring, String.class)
                    .getResultList();
            for (String s : subStringResultList) {
                System.out.println("s = " + s);
            }

            String queryLocate = "select locate('de', 'abcdefg') from Member m";
            List<Integer> locateResultList = em.createQuery(queryLocate, Integer.class)
                    .getResultList();
            for (Integer s : locateResultList) {
                System.out.println("s = " + s);
            }

            //SIZE
            String querySize = "select size(t.members) from Team t";
            List<Integer> sizeResultList = em.createQuery(querySize, Integer.class)
                    .getResultList();
            for (Integer s : sizeResultList) {
                System.out.println("s = " + s);
            }

            /**
             * 사용자 정의 함수 호출 - 하이버네이트는 사용전 방언에 추가해야 한다.
             *    -  사용하는 DB 방언을 상속 받고, 사용자 정의 함수를 등록한다.
             */
            String queryCustomFunction = "select function('group_concat', m.username) from Member m";
            List<String> customFunctionResultList = em.createQuery(queryCustomFunction, String.class)
                    .getResultList();
            for (String s : customFunctionResultList) {
                System.out.println("s = " + s);
            }



            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}
