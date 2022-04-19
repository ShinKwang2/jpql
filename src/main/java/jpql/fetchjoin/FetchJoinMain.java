package jpql.fetchjoin;

import jpql.Member;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class FetchJoinMain {
    /**Fetch Join
     *  - SQL 조인 종류X
     *  - JPQL 에서 성능 최적화를 위해 제공하는 기능
     *  - 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회하는 기능
     *  - join fetch 명령어 사용
     *  - 페치 조인 ::=[ LEFT [OUTER] | INNER ] JOIN FETCH 조인경로
     */

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

//            Team teamC = new Team();
//            teamC.setName("팀C");
//            em.persist(teamC);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.changeTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.changeTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.changeTeam(teamB);
            em.persist(member3);

//            Member member4 = new Member();
//            member4.setUsername("회원4");
//            em.persist(member4);

            em.flush();
            em.clear();

            String query = "select m from Member m join fetch m.team";
            List<Member> resultList = em.createQuery(query, Member.class)
                    .getResultList();
            for (Member member : resultList) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
            }

            //컬렉션 페치 조인 - 중복된 결과가 나옴
            String queryCollection = "select t from Team t join fetch t.members ";
            List<Team> collectionResultList = em.createQuery(queryCollection, Team.class)
                    .getResultList();
            for (Team team : collectionResultList) {
                System.out.println("team = " + team.getName() + "|members=" + team.getMembers().size());
                for ( Member member : team.getMembers() ) {
                    System.out.println(" -> member = " + member);
                }
            }

            //페치조인과 DISTINCT
            String queryDistinct = "select distinct t from Team t join fetch t.members";
            List<Team> distinctResultList = em.createQuery(queryDistinct, Team.class)
                    .getResultList();

            System.out.println("distinctResultList = " + distinctResultList.size());

            for (Team team : distinctResultList) {
                System.out.println("team = " + team.getName() + "|members=" + team.getMembers().size());
                for ( Member member : team.getMembers() ) {
                    System.out.println(" -> member = " + member);
                }
            }

            em.flush();
            em.clear();

            //일반 조인
            String queryJoin = "select t from Team t join t.members m";
            List<Team> joinResultList = em.createQuery(queryJoin, Team.class)
                    .getResultList();

            System.out.println("joinResultList = " + joinResultList.size());

            for (Team team : joinResultList) {
                System.out.println("team = " + team.getName() + "|members=" + team.getMembers().size());
                for ( Member member : team.getMembers() ) {
                    System.out.println(" -> member = " + member);
                }
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
