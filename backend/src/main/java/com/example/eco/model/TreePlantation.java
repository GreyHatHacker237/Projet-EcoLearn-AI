@Entity
public class TreePlantation {

    @Id
    @GeneratedValue(strategy = Ge@Entity
public class TreePlantation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private Integer treesPlanted;
    private Double carbonOffset;
}
nerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private Integer treesPlanted;
    private Double carbonOffset;
}
