package jpql.querywithentity;

import jpql.Member;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class QueryWIthEntityMain {

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

            em.flush();
            em.clear();

            /**
             * 엔티티 직접 사용 - 기본 키 값 관련
             */
            //엔티티 직접 사용
            String query = "select m from Member m where m = :member";
            Member findMember = em.createQuery(query, Member.class)
                    .setParameter("member", member1)
                    .getSingleResult();
            System.out.println("findMember = " + findMember);

            //기본 키 값 사용
            String queryWithId = "select m from Member m where m.id = :memberId";
            Member findMemberWithId = em.createQuery(queryWithId, Member.class)
                    .setParameter("memberId", member1.getId())
                    .getSingleResult();
            System.out.println("findMemberWithId = " + findMemberWithId);

            /**
             * 엔티티 직접 사용 - 외래 키 값 관련
             */
            //엔티티 직접 사용
            String queryFKEntitiy = "select m from Member m where m.team = :team";
            List<Member> members = em.createQuery(queryFKEntitiy, Member.class)
                    .setParameter("team", teamA)
                    .getResultList();
            for (Member member : members) {
                System.out.println("member = " + member);
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
