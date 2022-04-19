package jpql.pathexpression;

import jpql.Member;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Collection;
import java.util.List;

public class PathMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("팀A");
            em.persist(team);

            Member member1 = new Member();
            member1.setUsername("관리자1");
            member1.changeTeam(team);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("관리자2");
            member2.changeTeam(team);
            em.persist(member2);

            em.flush();
            em.clear();

            //상태필드(state field) : 경로 탐색의 끝, 탐색 X
            String query = "select m.username from Member m";
            List<String> result = em.createQuery(query, String.class)
                    .getResultList();
            for (String s : result) {
                System.out.println("s = " + s);
            }

            //단일 값 연관 경로: 묵시적 내부 조인(inner join) 발생, 탐색 O - 조심해서 사용
            String querySingleValue = "select m.team from Member m";
            List<Team> SingleValueresultList = em.createQuery(querySingleValue, Team.class)
                    .getResultList();
            for (Team s : SingleValueresultList) {
                System.out.println("s = " + s);
            }

            //컬렉션 값 연관 경로: 묵시적 내부 조인 발생, 탐색 X -조심해서 사용
            String queryCollectionValue = "select t.members from Team t";
            Collection resultList = em.createQuery(queryCollectionValue, Collection.class)
                    .getResultList();
            for (Object o : resultList) {
                System.out.println("o = " + o);
            }
            //컬렉션 값 연관 경로: size 정도만 가능
            String querySize = "select t.members.size from Team t";
            Integer singleResult = em.createQuery(querySize, Integer.class)
                    .getSingleResult();
            System.out.println("singleResult = " + singleResult);
            //명시적 조인을 통해 별칭으로 탐색 가능
            String querySearch = "select m.username From Team t join t.members m";
            List<String> searchResult = em.createQuery(querySearch, String.class)
                    .getResultList();

            //실무에서는 묵시적 조인 X, 명시적 조인 사용

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
