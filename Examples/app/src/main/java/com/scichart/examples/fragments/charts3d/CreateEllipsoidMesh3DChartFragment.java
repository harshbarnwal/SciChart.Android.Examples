package com.scichart.examples.fragments.charts3d;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting3d.common.math.Vector3;
import com.scichart.charting3d.model.dataSeries.freeSurface.EllipsoidDataSeries3D;
import com.scichart.charting3d.visuals.SciChartSurface3D;
import com.scichart.charting3d.visuals.axes.NumericAxis3D;
import com.scichart.charting3d.visuals.camera.Camera3D;
import com.scichart.charting3d.visuals.renderableSeries.data.DrawMeshAs;
import com.scichart.charting3d.visuals.renderableSeries.data.GradientColorPalette;
import com.scichart.charting3d.visuals.renderableSeries.freeSurface.FreeSurfacePaletteMinMaxMode;
import com.scichart.charting3d.visuals.renderableSeries.freeSurface.FreeSurfaceRenderableSeries3D;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.examples.R;
import com.scichart.examples.components.SpinnerStringAdapter;
import com.scichart.examples.fragments.base.ExampleBaseFragment;
import com.scichart.examples.utils.ItemSelectedListenerBase;

import java.util.Random;

import butterknife.BindView;

import static com.scichart.drawing.utility.ColorUtil.Blue;
import static com.scichart.drawing.utility.ColorUtil.Cyan;
import static com.scichart.drawing.utility.ColorUtil.DarkRed;
import static com.scichart.drawing.utility.ColorUtil.GreenYellow;
import static com.scichart.drawing.utility.ColorUtil.Red;
import static com.scichart.drawing.utility.ColorUtil.Yellow;

public class CreateEllipsoidMesh3DChartFragment extends ExampleBaseFragment {
    @BindView(R.id.chart3d)
    SciChartSurface3D surface3d;

    @BindView(R.id.paletteModeSelector)
    Spinner paletteModeSelector;

    @Override
    protected int getLayoutId() {
        return R.layout.example_create_free_surface_3d_fragment;
    }

    @Override
    protected void initExample() {
        final Camera3D camera = sciChart3DBuilder.newCamera3D().build();

        surface3d.getWorldDimensions().assign(200, 200, 200);

        final NumericAxis3D xAxis = sciChart3DBuilder.newNumericAxis3D().withVisibleRange(-7, 7).withAutoRangeMode(AutoRange.Never).build();
        final NumericAxis3D yAxis = sciChart3DBuilder.newNumericAxis3D().withVisibleRange(-7, 7).withAutoRangeMode(AutoRange.Never).build();
        final NumericAxis3D zAxis = sciChart3DBuilder.newNumericAxis3D().withVisibleRange(-7, 7).withAutoRangeMode(AutoRange.Never).build();

        final int sizeU = 40, sizeV = 20;

        final EllipsoidDataSeries3D<Double> meshDataSeries = new EllipsoidDataSeries3D<>(Double.class, sizeU, sizeV);
        meshDataSeries.setA(6d);
        meshDataSeries.setB(6d);
        meshDataSeries.setC(6d);

        final Random random = new Random();
        for (int u = 0; u < sizeU; u++) {
            for (int v = 0; v < sizeV; v++) {
                final double weight = 1d - Math.abs(2d * v / sizeV - 1d);
                final double offset = random.nextDouble();

                meshDataSeries.setDisplacement(u, v, offset * weight);
            }
        }

        final int[] colors = {0xFF1D2C6B, Blue, Cyan, GreenYellow, Yellow, Red, DarkRed};
        final float[] stops = {0, .1f, .3f, .5f, .7f, .9f, 1};

        final FreeSurfaceRenderableSeries3D rs = sciChart3DBuilder.newFreeSurfaceSeries3D()
                .withDataSeries(meshDataSeries)
                .withDrawMeshAs(DrawMeshAs.SolidWireframe)
                .withStroke(0x77228B22)
                .withContourInterval(0.1f)
                .withContourStroke(0x77228B22)
                .withStrokeThicknes(1f)
                .withLightingFactor(0.8f)
                .withMeshColorPalette(new GradientColorPalette(colors, stops))
                .build();
        UpdateSuspender.using(surface3d, new Runnable() {
            @Override
            public void run() {
                surface3d.setCamera(camera);

                surface3d.setXAxis(xAxis);
                surface3d.setYAxis(yAxis);
                surface3d.setZAxis(zAxis);

                surface3d.getRenderableSeries().add(rs);

                surface3d.getChartModifiers().add(sciChart3DBuilder.newModifierGroupWithDefaultModifiers().build());
            }
        });

        paletteModeSelector.setAdapter(new SpinnerStringAdapter(getActivity(), R.array.palette_mode_list));
        paletteModeSelector.setSelection(0);
        paletteModeSelector.setOnItemSelectedListener(new ItemSelectedListenerBase() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switchPaletteMode(rs, position);
            }
        });
    }

    private void switchPaletteMode(FreeSurfaceRenderableSeries3D rs, int paletteMode) {
        switch (paletteMode) {
            // Radial
            case 0:
                rs.setPaletteMinMaxMode(FreeSurfacePaletteMinMaxMode.Relative);
                rs.setPaletteMinimum(new Vector3(0f, 6f, 0f));
                rs.setPaletteMaximum(new Vector3(0f, 7f, 0f));
                rs.setPaletteRadialFactor(1f);
                rs.setPaletteAxialFactor(new Vector3(0, 0, 0));
                rs.setPaletteAzimuthalFactor(0f);
                rs.setPalettePolarFactor(0f);
                break;
            // Axial
            case 1:
                rs.setPaletteMinMaxMode(FreeSurfacePaletteMinMaxMode.Absolute);
                rs.setPaletteMinimum(new Vector3(0f, -4f, 0f));
                rs.setPaletteMaximum(new Vector3(0f, 4f, 0f));
                rs.setPaletteRadialFactor(0f);
                rs.setPaletteAxialFactor(new Vector3(0f, 1f, 0f));
                rs.setPaletteAzimuthalFactor(0f);
                rs.setPalettePolarFactor(0f);
                break;
            // Azimuthal
            case 2:
                rs.setPaletteRadialFactor(0f);
                rs.setPaletteAxialFactor(new Vector3(0f, 0f, 0f));
                rs.setPaletteAzimuthalFactor(1f);
                rs.setPalettePolarFactor(0f);
                break;
            // Polar
            case 3:
                rs.setPaletteRadialFactor(0f);
                rs.setPaletteAxialFactor(new Vector3(0f, 0f, 0f));
                rs.setPaletteAzimuthalFactor(0f);
                rs.setPalettePolarFactor(1f);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
