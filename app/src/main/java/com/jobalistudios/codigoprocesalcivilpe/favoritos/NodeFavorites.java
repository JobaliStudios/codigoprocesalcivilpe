package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.R;
import com.jobalistudios.codigoprocesalcivilpe.navigation.LegalHierarchyRepository;

/** Alta/baja de favoritos desde las pantallas de navegación y de contenido. */
public final class NodeFavorites {

    private NodeFavorites() {
    }

    /** Item de favorito equivalente a un nodo del árbol. */
    public static FavoriteItem itemForNode(LegalHierarchyRepository.Node node) {
        String destination = FavoriteDestinationMapper.destinationForNode(node.id);
        String title = node.subtitle == null || node.subtitle.isEmpty()
                ? node.title
                : node.title + " — " + node.subtitle;
        return new FavoriteItem(destination, title, node.articleRange, typeLabel(node.type), destination);
    }

    /** Conecta un botón estrella para alternar el favorito del nodo; se oculta si no hay nodo. */
    public static void bindToggle(@Nullable ImageButton button, @Nullable LegalHierarchyRepository.Node node) {
        if (button == null) {
            return;
        }
        if (node == null) {
            button.setVisibility(View.GONE);
            return;
        }
        Context context = button.getContext();
        FavoritesManager manager = new FavoritesManager(context);
        FavoriteItem item = itemForNode(node);
        render(button, manager.isFavorite(item.getId()));
        button.setOnClickListener(v -> {
            manager.toggle(item);
            boolean favorite = manager.isFavorite(item.getId());
            render(button, favorite);
            Toast.makeText(context, favorite
                    ? R.string.favorite_added_message
                    : R.string.favorite_removed_message, Toast.LENGTH_SHORT).show();
        });
    }

    private static void render(ImageButton button, boolean favorite) {
        button.setImageResource(favorite
                ? R.drawable.baseline_star_24
                : R.drawable.baseline_star_border_24);
        button.setContentDescription(button.getContext().getString(favorite
                ? R.string.favorite_toggle_remove
                : R.string.favorite_toggle_add));
    }

    private static String typeLabel(String nodeType) {
        if ("SECCION".equalsIgnoreCase(nodeType)) return "Sección";
        if ("TITULO".equalsIgnoreCase(nodeType)) return "Título";
        if ("CAPITULO".equalsIgnoreCase(nodeType)) return "Capítulo";
        if ("SUBCAPITULO".equalsIgnoreCase(nodeType)) return "Subcapítulo";
        return nodeType;
    }
}
