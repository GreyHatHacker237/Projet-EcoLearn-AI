@Entity
public class CarbonMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private Double sessionCarbon;
    private Double totalCarbon;

    private LocalDate date;
}
