package org.shopouille.seeder;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.text.Normalizer;
import java.time.Instant;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.shopouille.entity.Product;

@ApplicationScoped
public class DataSeeder {

    private static final Logger LOGGER = Logger.getLogger("DataSeeder");

    @ConfigProperty(name = "quarkus.profile")
    String activeProfile;

    private final String[] productNames = {
            "Lampe Ambiante Aurora",
            "Casque Audio NovaX Pro",
            "Sac à Dos UrbanTrail 25L",
            "Montre Connectée PulseWave",
            "Gourde Isotherme FrostSteel",
            "Enceinte Bluetooth EchoSphere Mini",
            "Tapis de Souris Ergonomique GlidePro",
            "Chargeur Sans Fil VoltCharge X",
            "Carnet Premium SoftTouch",
            "Set de Tasses Céramique Moonstone",
            "Oreiller Mémoire de Forme CloudRest",
            "Bougie Parfumée Amber & Cedar",
            "Câble USB-C UltraDurable 2m",
            "Support Smartphone FlexiStand",
            "Plaid Polaire HyperSoft",
            "Housse Ordinateur SlimShield 15",
            "Clavier Sans Fil KeyLite",
            "Lampe de Bureau VisionGlow",
            "Sac Banane TravelEase",
            "Mini Drone SkyRider Pocket"
    };

    @Transactional
    public void seed(@Observes StartupEvent startupEvent) {
        if (!"dev".equals(activeProfile)) {
            LOGGER.info("Skip seeder");
            return;
        }

        if (Product.count() > 0) {
            LOGGER.info("Product already fill with data");
            return;
        }

        for (String name : productNames) {
            LOGGER.info("Add product: " + name);
            Product p = new Product();
            p.title = name;
            p.slug = toSlug(name);
            p.descriptionHtml = "Découvrez " + name
                    + ", un produit conçu pour offrir confort, performance et qualité au quotidien.";
            p.seoTitle = name + " | Shopouille";
            p.seoDescription = "Découvrez " + name + ", un produit premium sélectionné pour qualité et style.";

            p.isActive = true;
            p.publishedAt = Instant.now();

            p.persist();
            LOGGER.info("Product added");
        }
    }

    private String toSlug(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+)|(-+$)", "");
    }
}
