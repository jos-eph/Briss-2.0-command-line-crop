/**
 * Copyright 2010 Gerhard Aigner
 * <p>
 * This file is part of BRISS.
 * <p>
 * BRISS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * BRISS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * BRISS. If not, see http://www.gnu.org/licenses/.
 */
package at.laborg.briss.cli;

import at.laborg.briss.cli.utilities.CommandValues;
import at.laborg.briss.exception.CropException;
import at.laborg.briss.model.ClusterDefinition;
import at.laborg.briss.model.CropDefinition;
import at.laborg.briss.model.CropFinder;
import at.laborg.briss.model.PageCluster;
import at.laborg.briss.model.SplitFinder;
import at.laborg.briss.utils.ClusterCreator;
import at.laborg.briss.utils.ClusterRenderWorker;
import at.laborg.briss.utils.DocumentCropper;
import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.util.ArrayList;

public final class BrissCMD {

	private BrissCMD() {
	}

	public static void autoCrop(final String[] args) {

		CommandValues workDescription = CommandValues.parseToWorkDescription(args);

		if (!CommandValues.isValidJob(workDescription))
			return;

		System.out.println("Clustering PDF: " + workDescription.getSourceFile());
		ClusterDefinition clusterDefinition = null;

		String password = workDescription.getPassword();

		try {
			clusterDefinition = ClusterCreator.clusterPages(workDescription.getSourceFile(), password, null);
		} catch (IOException e1) {
			System.out.println("Error occurred while clustering.");
			e1.printStackTrace(System.out);
			System.exit(-1);
		}
		System.out.println("Created " + clusterDefinition.getClusterList().size() + " clusters.");

		ClusterRenderWorker cRW = new ClusterRenderWorker(workDescription.getSourceFile(), password, clusterDefinition);
		cRW.start();

		System.out.print("Starting to render clusters.");
		while (cRW.isAlive()) {
			System.out.print(".");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("finished!");
		System.out.println("Calculating crop rectangles.");
		try {
			for (PageCluster cluster : clusterDefinition.getClusterList()) {
				float[] auto = CropFinder.getAutoCropFloats(cluster.getImageData().getPreviewImage());

				ArrayList<float[]> ratios = new ArrayList<>();
				ratios.add(auto);

				if (workDescription.isSplitColumns()) {
					ArrayList<float[]> newRatios = new ArrayList<>();
					for (float[] crop : ratios) {
						newRatios.addAll(SplitFinder.splitColumn(cluster.getImageData().getPreviewImage(), crop));
					}
					ratios = newRatios;
				}

				if (workDescription.isSplitRows()) {
					ArrayList<float[]> newRatios = new ArrayList<>();
					for (float[] crop : ratios) {
						newRatios.addAll(SplitFinder.splitRow(cluster.getImageData().getPreviewImage(), crop));
					}
					ratios = newRatios;
				}

				for (float[] ratio : ratios) {
					cluster.addRatios(ratio);
				}
			}
			CropDefinition cropDefintion = CropDefinition.createCropDefinition(workDescription.getSourceFile(),
					workDescription.getDestFile(), clusterDefinition);
			System.out.println("Starting to crop files.");
			DocumentCropper.crop(cropDefintion, password);
			System.out.println("Cropping succesful. Cropped to:" + workDescription.getDestFile().getAbsolutePath());
		} catch (IOException | DocumentException | IllegalArgumentException e) {
			e.printStackTrace();
		} catch (CropException e) {
			System.out.println("Error while cropping:" + e.getMessage());
		}
	}

}
